package database;

import database.models.*;
import logic.Configuration;
import network.Problem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.function.Supplier;

/** Each request runs in one transaction, so a refused or failed request changes nothing. */
public final class DataManager {
    private static final Logger log = LogManager.getLogger(DataManager.class);
    private static final String DEFAULT_URL = "jdbc:h2:file:./data/campus;DB_CLOSE_ON_EXIT=TRUE";
    private static final ThreadLocal<Session> CURRENT = new ThreadLocal<>();
    private static SessionFactory sessionFactory;

    private DataManager() {
    }

    public static void connect() {
        var configuration = new org.hibernate.cfg.Configuration()
                .setProperty("hibernate.connection.url", Configuration.setting("portal.db.url", "PORTAL_DB_URL", DEFAULT_URL))
                .setProperty("hibernate.connection.username", Configuration.setting("portal.db.user", "PORTAL_DB_USER", "sa"))
                .setProperty("hibernate.connection.password", Configuration.setting("portal.db.password", "PORTAL_DB_PASSWORD", ""))
                .setProperty("hibernate.connection.pool_size", "10")
                .setProperty("hibernate.hbm2ddl.auto", "update");
        for (Class<?> model : List.of(User.class, Department.class, Master.class, Student.class, Course.class,
                Score.class, UniRequest.class, Chat.class, Message.class, CourseContent.class, ContentItem.class,
                Exercise.class, Answer.class, StoredFile.class)) {
            configuration.addAnnotatedClass(model);
        }
        sessionFactory = configuration.buildSessionFactory();
        log.info("Connected to the database");
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public static boolean isConnected() {
        try {
            return sessionFactory.isOpen() && inTransaction(() ->
                    session().createNativeQuery("select 1", Integer.class).getSingleResult() == 1);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void inTransaction(Runnable work) {
        inTransaction(() -> {
            work.run();
            return null;
        });
    }

    public static <T> T inTransaction(Supplier<T> work) {
        if (CURRENT.get() != null) {
            return work.get();
        }
        try (Session session = sessionFactory.openSession()) {
            CURRENT.set(session);
            Transaction transaction = session.beginTransaction();
            try {
                T result = work.get();
                transaction.commit();
                return result;
            } catch (RuntimeException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        } finally {
            CURRENT.remove();
        }
    }

    public static Session session() {
        return CURRENT.get();
    }

    public static <T> T get(Class<T> type, int id) {
        T value = session().find(type, id);
        if (value == null) {
            throw new Problem("That " + type.getSimpleName().toLowerCase() + " no longer exists.");
        }
        return value;
    }

    public static <T> List<T> all(Class<T> type) {
        return session().createQuery("from " + type.getSimpleName(), type).list();
    }

    /** Runs a query whose parameters are named p0, p1 and so on. */
    public static <T> List<T> query(Class<T> type, String hql, Object... parameters) {
        Query<T> query = session().createQuery(hql, type);
        for (int i = 0; i < parameters.length; i++) {
            query.setParameter("p" + i, parameters[i]);
        }
        return query.list();
    }

    public static <T> T first(Class<T> type, String hql, Object... parameters) {
        List<T> results = query(type, hql, parameters);
        return results.isEmpty() ? null : results.get(0);
    }

    public static void save(Object entity) {
        session().persist(entity);
    }

    public static void delete(Object entity) {
        session().remove(entity);
    }
}
