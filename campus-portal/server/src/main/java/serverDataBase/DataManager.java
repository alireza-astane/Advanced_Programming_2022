package serverDataBase;

import logic.Configuration;
import logic.enums.*;
import network.Passwords;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import serverDataBase.models.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

/**
 * All database access. Each client request runs in one transaction on its own session,
 * so entities stay attached while a page is built and a failed request changes nothing.
 */
public class DataManager {
    static Logger log = LogManager.getLogger(DataManager.class);
    private static final String DEFAULT_URL = "jdbc:h2:file:./data/portal;DB_CLOSE_ON_EXIT=TRUE";
    private static final ThreadLocal<Session> CURRENT = new ThreadLocal<>();
    private static final Random RANDOM = new Random();

    public static SessionFactory sessionFactory;

    public static void connect() {
        log.info("Connecting to the database");
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration()
                .setProperty("hibernate.connection.url",
                        Configuration.setting("portal.db.url", "PORTAL_DB_URL", DEFAULT_URL))
                .setProperty("hibernate.connection.username",
                        Configuration.setting("portal.db.user", "PORTAL_DB_USER", "sa"))
                .setProperty("hibernate.connection.password",
                        Configuration.setting("portal.db.password", "PORTAL_DB_PASSWORD", ""))
                .setProperty("hibernate.connection.pool_size", "10")
                .setProperty("hibernate.hbm2ddl.auto", "update");
        for (Class<?> model : List.of(User.class, Student.class, Master.class, Department.class, Course.class,
                Score.class, UniRequest.class, Chat.class, Message.class, EducationalContext.class,
                Exercise.class, Answer.class)) {
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
            return sessionFactory.isOpen() && read(() ->
                    session().createNativeQuery("select 1", Integer.class).getSingleResult() == 1);
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void inTransaction(Runnable work) {
        read(() -> {
            work.run();
            return null;
        });
    }

    public static <T> T read(Supplier<T> work) {
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
        Session session = CURRENT.get();
        if (session == null) {
            throw new IllegalStateException("No transaction is open");
        }
        return session;
    }

    private static <T> T required(T value, String what) {
        if (value == null) {
            throw new IllegalArgumentException("No such " + what);
        }
        return value;
    }

    public static User getUserById(int id) {
        return session().find(User.class, id);
    }

    public static Chat getChatById(int id) {
        return session().find(Chat.class, id);
    }

    public static Department getDepartmentById(int id) {
        return session().find(Department.class, id);
    }

    public static Course getCourseById(int id) {
        return session().find(Course.class, id);
    }

    public static EducationalContext getEducationalContextById(int id) {
        return session().find(EducationalContext.class, id);
    }

    public static Exercise getExerciseById(int id) {
        return session().find(Exercise.class, id);
    }

    public static Answer getAnswerById(int id) {
        return session().find(Answer.class, id);
    }

    public static Score getScoreById(int id) {
        return session().find(Score.class, id);
    }

    public static UniRequest getUniRequestById(int id) {
        return session().find(UniRequest.class, id);
    }

    public static List<User> getUsers() {
        return all(User.class);
    }

    public static ArrayList<Course> getCourses() {
        return all(Course.class);
    }

    public static ArrayList<Master> getMasters() {
        return all(Master.class);
    }

    public static ArrayList<Student> getStudents() {
        return all(Student.class);
    }

    public static ArrayList<Department> getDepartments() {
        return all(Department.class);
    }

    private static <T> ArrayList<T> all(Class<T> type) {
        return new ArrayList<>(session().createQuery("from " + type.getSimpleName(), type).list());
    }

    public static User getUserByUserName(String username) {
        return session().createQuery("from User where username = :username", User.class)
                .setParameter("username", username).uniqueResult();
    }

    /** The courses a user attends or, for professors, teaches. */
    public static List<Course> coursesOf(User user) {
        if (user.getStudent() != null) {
            return user.getStudent().getStudentCourses();
        }
        return user.getMaster() != null ? user.getMaster().getMasterCourses() : List.of();
    }

    private static Student studentByNumber(String number) {
        return required(session().createQuery("from Student where studentshipNumber = :number", Student.class)
                .setParameter("number", number).uniqueResult(), "student");
    }

    private static Student student(int id) {
        return required(session().find(Student.class, id), "student");
    }

    private static Course course(int id) {
        return required(getCourseById(id), "course");
    }

    private static Department departmentOf(Student student) {
        return required(getDepartmentById(student.getStudentDepartmentId()), "department");
    }

    private static UniRequest addRequest(int requesterId, UniRequestType type, UniRequestResultType result,
                                         String context, Integer responsibleId) {
        UniRequest request = new UniRequest();
        request.setUniRequesterId(requesterId);
        request.setUniRequestTime(LocalDateTime.now().toString());
        request.setUniRequestType(type);
        request.setUniRequestResult(result);
        request.setUniRequestContext(context);
        session().persist(request);
        getUserById(requesterId).getOutUniRequests().add(request);
        if (responsibleId != null) {
            request.setUniRequestResponsibleId(responsibleId);
            required(getUserById(responsibleId), "user").getInUniRequests().add(request);
        }
        return request;
    }

    private static String label(String key, String... values) {
        String text = Configuration.labels.getProperty(key);
        for (int i = 0; i < values.length; i++) {
            text = text.replace("@" + (i + 1), values[i]);
        }
        return text;
    }

    public static void addRecommendationRequest(int id, int masterId) {
        User user = getUserById(id);
        if (required(getUserById(masterId), "professor").getMaster() == null) {
            throw new IllegalArgumentException("That user is not a professor");
        }
        addRequest(id, UniRequestType.RECOMMENDATION, UniRequestResultType.NOT_ANSWERED,
                label("RecommendationRequest", user.getUserFullName(), user.getStudent().getStudentshipNumber()), masterId);
    }

    public static void addEnrollmentRequest(int id) {
        User user = getUserById(id);
        Student student = user.getStudent();
        addRequest(id, UniRequestType.ENROLLMENT, UniRequestResultType.ACCEPTED,
                label("Enrollment", user.getUserFullName(), student.getStudentshipNumber(),
                        departmentOf(student).getDepartmentType().name,
                        LocalDateTime.now().plusYears(1).toLocalDate().toString()), null);
    }

    public static void addMinorRequest(int id, int departmentId) {
        User user = getUserById(id);
        Student student = user.getStudent();
        Department target = required(getDepartmentById(departmentId), "department");
        addRequest(id, UniRequestType.MINOR, UniRequestResultType.NOT_ANSWERED,
                label("MinorRequest", user.getUserFullName(), student.getStudentshipNumber(), target.getDepartmentType().name),
                departmentOf(student).getDepartmentAssistantId());
    }

    public static void addWithdrawRequest(int id) {
        User user = getUserById(id);
        Student student = user.getStudent();
        addRequest(id, UniRequestType.WITHDRAWAL, UniRequestResultType.NOT_ANSWERED,
                label("WithDrawRequest", user.getUserFullName(), student.getStudentshipNumber()),
                departmentOf(student).getDepartmentAssistantId());
    }

    public static void addDormitoryRequest(int id) {
        boolean granted = RANDOM.nextBoolean();
        addRequest(id, UniRequestType.DORMITORY,
                granted ? UniRequestResultType.ACCEPTED : UniRequestResultType.REJECTED,
                granted ? "A dormitory place was assigned." : "No dormitory place is available.", null);
    }

    public static void addDDDRequest(int id) {
        addRequest(id, UniRequestType.DOCTORAL_DISSERTATION_DEFENSE, UniRequestResultType.ACCEPTED,
                label("DDDRequest", LocalDateTime.now().plusMonths(1).toLocalDate().toString()), null);
    }

    public static void addCreateChatRequest(int id, String shipNumber) {
        Integer target = session().createQuery(
                        "select s.id from Student s where s.studentshipNumber = :number", Integer.class)
                .setParameter("number", shipNumber).uniqueResult();
        if (target == null) {
            target = session().createQuery(
                            "select m.id from Master m where m.masterShipNumber = :number", Integer.class)
                    .setParameter("number", shipNumber).uniqueResult();
        }
        addRequest(id, UniRequestType.CREATE_CHAT, UniRequestResultType.NOT_ANSWERED,
                getUserById(id).getUserFullName() + " would like to chat with you.", required(target, "user"));
    }

    public static void requestUnitSelectionFromAssistant(int id, int courseId) {
        Student student = getUserById(id).getStudent();
        addRequest(id, UniRequestType.UNITSELCTION, UniRequestResultType.NOT_ANSWERED,
                String.valueOf(course(courseId).getCourseId()), departmentOf(student).getDepartmentAssistantId());
    }

    public static void answerUniRequest(int requestId, boolean accepted) {
        UniRequest request = required(getUniRequestById(requestId), "request");
        request.setUniRequestResult(accepted ? UniRequestResultType.ACCEPTED : UniRequestResultType.REJECTED);
        if (!accepted) {
            return;
        }
        int requester = request.getUniRequesterId();
        switch (request.getUniRequestType()) {
            case CREATE_CHAT -> chatBetween(requester, request.getUniRequestResponsibleId());
            case WITHDRAWAL -> student(requester).setStudentState(StudentState.WITHDREW);
            case UNITSELCTION -> addIfAbsent(student(requester).getStudentPreRegisteredCourses(),
                    course(Integer.parseInt(request.getUniRequestContext())));
            default -> {
            }
        }
    }

    private static <T> void addIfAbsent(List<T> list, T value) {
        if (!list.contains(value)) {
            list.add(value);
        }
    }

    public static Chat chatBetween(int firstId, int secondId) {
        User first = required(getUserById(firstId), "user");
        User second = required(getUserById(secondId), "user");
        for (Chat chat : first.getUserChats()) {
            if (chat.getChatUsers().size() == 2 && chat.getChatUsers().contains(second)) {
                return chat;
            }
        }
        Chat chat = new Chat();
        chat.setLastMessage("");
        chat.setLastMessageTime(LocalDateTime.now());
        chat.getChatUsers().add(first);
        chat.getChatUsers().add(second);
        session().persist(chat);
        first.getUserChats().add(chat);
        second.getUserChats().add(chat);
        return chat;
    }

    public static void addMessage(int senderId, int chatId, String text, byte[] file) {
        Chat chat = required(getChatById(chatId), "chat");
        Message message = new Message();
        message.setMessageContent(text);
        message.setMessageChatId(chatId);
        message.setMessageTime(LocalDateTime.now());
        message.setMessageSenderId(senderId);
        message.setMessageType(file != null ? MessageType.FILE : MessageType.TEXT);
        message.setMessageFile(file);
        session().persist(message);
        chat.getMessages().add(message);
        chat.setLastMessage(text);
        chat.setLastMessageTime(message.getMessageTime());
    }

    public static void addMessageToAdmin(int senderId, String text) {
        User admin = required(session().createQuery("from User where userType = :type order by id", User.class)
                .setParameter("type", UserType.ADMIN).setMaxResults(1).uniqueResult(), "administrator");
        addMessage(senderId, chatBetween(senderId, admin.getId()).getChatId(), text, null);
    }

    public static void addCourse(int departmentId, List<String> args) {
        Course course = new Course();
        course.setCourseDepartmentId(departmentId);
        course.setTerm(Configuration.logics.getProperty("currentTerm"));
        fillCourse(course, args.get(0), args.get(1), args.get(2), args.subList(3, args.size()));
        session().persist(course);
        required(getDepartmentById(departmentId), "department").getDepartmentCourses().add(course);
        master(course.getCourseMasterId()).getMasterCourses().add(course);
    }

    public static void editCourse(List<String> args) {
        Course course = course(Integer.parseInt(args.get(0)));
        int oldMaster = course.getCourseMasterId();
        fillCourse(course, args.get(1), args.get(2), args.get(4), args.subList(5, args.size()));
        if (oldMaster != course.getCourseMasterId()) {
            master(oldMaster).getMasterCourses().remove(course);
            master(course.getCourseMasterId()).getMasterCourses().add(course);
        }
    }

    /** {@code rest} holds plan, exam time, capacity, type, units, prerequisite and corequisite. */
    private static void fillCourse(Course course, String name, String number, String masterId, List<String> rest) {
        course.setCourseName(name);
        course.setCourseNumber(number);
        course.setCourseMasterId(master(Integer.parseInt(masterId)).getId());
        course.setCourseWeaklyPlan(parseList(rest.get(0)));
        course.setCourseExamTime(LocalDateTime.parse(rest.get(1)));
        course.setCourseCapacity(Integer.parseInt(rest.get(2)));
        course.setCourseType(CourseType.valueOf(rest.get(3)));
        course.setCourseUnits(Integer.parseInt(rest.get(4)));
        course.setCoursePrerequisites(courses(rest.get(5)));
        course.setCourseCorequisites(courses(rest.get(6)));
    }

    private static Master master(int id) {
        return required(session().find(Master.class, id), "professor");
    }

    private static ArrayList<String> parseList(String text) {
        ArrayList<String> items = new ArrayList<>();
        for (String item : text.replace("[", "").replace("]", "").split(",")) {
            if (!item.isBlank()) {
                items.add(item.trim());
            }
        }
        return items;
    }

    private static ArrayList<Course> courses(String ids) {
        ArrayList<Course> courses = new ArrayList<>();
        for (String id : parseList(ids)) {
            courses.add(course(Integer.parseInt(id)));
        }
        return courses;
    }

    public static void deleteCourse(int id) {
        Course course = course(id);
        for (Student student : getStudents()) {
            student.getStudentCourses().remove(course);
            student.getStudentAssistingCourses().remove(course);
            student.getStudentPreRegisteredCourses().remove(course);
            student.getStudentSelectedCourse().remove(course);
            student.getStudentScores().removeAll(course.getScores());
        }
        for (Course other : getCourses()) {
            other.getCoursePrerequisites().remove(course);
            other.getCourseCorequisites().remove(course);
            other.getCourseGroup().remove(course);
        }
        master(course.getCourseMasterId()).getMasterCourses().remove(course);
        getDepartmentById(course.getCourseDepartmentId()).getDepartmentCourses().remove(course);
        session().remove(course);
    }

    public static void addMaster(int departmentId, List<String> args, byte[] image) {
        User user = new User();
        user.setUserType(UserType.MASTER);
        user.setUsername(args.get(4));
        user.setUserFullName(args.get(5));
        user.setUserEmail(args.get(6));
        user.setUserPhoneNumber(args.get(7));
        user.setUserNationalNumber(args.get(8));
        user.setUserPassword(Passwords.hash(args.get(9)));
        user.setUserLastLoginTime(LocalDateTime.now());
        user.setCanLogin(true);
        user.setUserImage(image);
        session().persist(user);
        Master master = new Master();
        master.setUser(user);
        user.setMaster(master);
        master.setMasterDepartmentId(departmentId);
        fillMaster(master, args.get(0), args.get(1), args.get(2), args.get(3));
        session().persist(master);
        required(getDepartmentById(departmentId), "department").getDepartmentMasters().add(master);
    }

    public static void editMaster(List<String> args, byte[] image) {
        Master master = master(Integer.parseInt(args.get(0)));
        fillMaster(master, args.get(1), args.get(2), args.get(3), args.get(4));
        User user = master.getUser();
        user.setUsername(args.get(6));
        user.setUserFullName(args.get(7));
        user.setUserEmail(args.get(9));
        user.setUserPhoneNumber(args.get(10));
        user.setUserNationalNumber(args.get(11));
        if (image != null) {
            user.setUserImage(image);
        }
    }

    private static void fillMaster(Master master, String type, String grade, String number, String room) {
        master.setMasterType(MasterType.valueOf(type));
        master.setMasterGrade(MasterGrade.valueOf(grade));
        master.setMasterShipNumber(number);
        master.setMasterRoomNumber(room);
    }

    public static void deleteMaster(int id) {
        Master master = master(id);
        if (!master.getMasterCourses().isEmpty()) {
            throw new IllegalStateException("This professor still has courses");
        }
        User user = master.getUser();
        getDepartmentById(master.getMasterDepartmentId()).getDepartmentMasters().remove(master);
        session().remove(master);
        user.setMaster(null);
        user.setCanLogin(false);
    }

    public static void addExercise(List<String> args, byte[] file) {
        Course course = course(Integer.parseInt(args.get(0)));
        Exercise exercise = new Exercise();
        exercise.setExerciseCourseId(course.getCourseId());
        exercise.setExerciseName(args.get(1));
        exercise.setExerciseStartTime(LocalDateTime.parse(args.get(2)));
        exercise.setExerciseEndTime(LocalDateTime.parse(args.get(3)));
        exercise.setExerciseDeadline(LocalDateTime.parse(args.get(4)));
        exercise.setExplanation(args.get(5));
        exercise.setExerciseType(ExerciseType.valueOf(args.get(6)));
        exercise.setExerciseFile(file);
        session().persist(exercise);
        course.getCourseExercises().add(exercise);
    }

    public static void addAnswer(int studentId, int exerciseId, String text, byte[] file) {
        Exercise exercise = required(getExerciseById(exerciseId), "exercise");
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exercise.getExerciseStartTime()) || now.isAfter(exercise.getExerciseEndTime())) {
            throw new IllegalStateException("This exercise is not open");
        }
        Answer answer = exercise.getExerciseAnswers().stream()
                .filter(existing -> existing.getAnswerStudentID() == studentId).findFirst().orElse(null);
        if (answer == null) {
            answer = new Answer();
            answer.setAnswerStudentID(studentId);
            answer.setAnswerExerciseId(exerciseId);
            answer.setAnswerUploadTime(now);
            session().persist(answer);
            exercise.getExerciseAnswers().add(answer);
        }
        answer.setAnswerUploadTime(now);
        answer.setAnswerText(text);
        answer.setAnswerFile(file);
    }

    public static void scoreAnswer(int answerId, String score) {
        required(getAnswerById(answerId), "answer").setAnswerScore(Double.parseDouble(score));
    }

    public static void addEducationalContext(int courseId, String name, String text, byte[] file) {
        Course course = course(courseId);
        EducationalContext context = new EducationalContext();
        context.setEducationalContextCourseId(courseId);
        fillContext(context, name, text, file);
        session().persist(context);
        course.getCourseEducationalContexts().add(context);
    }

    public static void editEducationalContext(int id, String name, String text, byte[] file) {
        fillContext(required(getEducationalContextById(id), "item"), name, text, file);
    }

    private static void fillContext(EducationalContext context, String name, String text, byte[] file) {
        context.setEducationalContextName(name);
        context.setEducationalContextText(text);
        context.setEducationalContextFile(file);
        context.setEducationalContextTime(LocalDateTime.now());
        context.setEducationalContextType(file != null ? EducationalContextType.FILE : EducationalContextType.TEXT);
    }

    public static void deleteEducationalContext(int id) {
        EducationalContext context = required(getEducationalContextById(id), "item");
        course(context.getEducationalContextCourseId()).getCourseEducationalContexts().remove(context);
        session().remove(context);
    }

    public static void setLastloginTime(int id) {
        getUserById(id).setUserLastLoginTime(LocalDateTime.now());
    }

    public static void changeEmail(int id, String email) {
        getUserById(id).setUserEmail(email);
    }

    public static void changePhoneNumber(int id, String phone) {
        getUserById(id).setUserPhoneNumber(phone);
    }

    public static void changePass(int id, String password) {
        User user = getUserById(id);
        user.setUserPassword(Passwords.hash(password));
        user.setUserLastLoginTime(LocalDateTime.now());
    }

    public static void addScoreProtestation(int scoreId, String text) {
        required(getScoreById(scoreId), "score").setScoreProtestation(text);
    }

    public static void addScoreProtestationAnswer(int scoreId, String text) {
        required(getScoreById(scoreId), "score").setScoreProtestationAnswer(text);
    }

    public static void setScoreValue(int scoreId, String value) {
        double parsed = Double.parseDouble(value);
        if (parsed < 0 || parsed > 20) {
            throw new IllegalArgumentException("Scores are between 0 and 20");
        }
        Score score = required(getScoreById(scoreId), "score");
        if (score.getScoreType() == ScoreType.VERIFIED) {
            throw new IllegalStateException("This score is final");
        }
        score.setScoreValue(Math.round(4 * parsed) / 4.0);
        score.setScoreType(ScoreType.TEMPORARY);
    }

    public static void verifyScores(int courseId) {
        for (Score score : course(courseId).getScores()) {
            if (score.getScoreType() == ScoreType.TEMPORARY) {
                score.setScoreType(ScoreType.VERIFIED);
            }
        }
    }

    public static void starOrUnstarCourse(int studentId, int courseId) {
        List<Course> starred = student(studentId).getStudentSelectedCourse();
        Course course = course(courseId);
        if (!starred.remove(course)) {
            starred.add(course);
        }
    }

    public static void preRegisterCourse(int studentId, int courseId) {
        addIfAbsent(student(studentId).getStudentPreRegisteredCourses(), course(courseId));
    }

    public static void undoPreRegisterCourse(int studentId, int courseId) {
        student(studentId).getStudentPreRegisteredCourses().remove(course(courseId));
    }

    public static void addTAToCourse(String studentNumber, int courseId) {
        addIfAbsent(studentByNumber(studentNumber).getStudentAssistingCourses(), course(courseId));
    }

    public static void addStudentToCourse(String studentNumber, int courseId) {
        Student student = studentByNumber(studentNumber);
        Course course = course(courseId);
        addIfAbsent(student.getStudentCourses(), course);
        if (course.getScores().stream().noneMatch(s -> s.getScoreStudentId() == student.getId())) {
            addScore(student, course);
        }
    }

    public static Score addScore(Student student, Course course) {
        Score score = new Score();
        score.setScoreCourseId(course.getCourseId());
        score.setScoreStudentId(student.getId());
        score.setUnits(course.getCourseUnits());
        session().persist(score);
        course.getScores().add(score);
        student.getStudentScores().add(score);
        return score;
    }

    public static void changeGroup(int studentId, int fromCourseId, int toCourseId) {
        Student student = student(studentId);
        Course from = course(fromCourseId);
        Course to = course(toCourseId);
        if (!from.getCourseGroup().contains(to)) {
            throw new IllegalArgumentException("Those are not groups of the same course");
        }
        if (student.getStudentPreRegisteredCourses().remove(from)) {
            addIfAbsent(student.getStudentPreRegisteredCourses(), to);
        }
    }

    /** Arguments are student type (or ALL), entry year (or 0) and the opening time. */
    public static void setUnitSelectionTime(int departmentId, List<String> args) {
        LocalDateTime time = LocalDateTime.parse(args.get(2));
        int year = Integer.parseInt(args.get(1));
        for (Student student : getStudents()) {
            boolean typeMatches = Objects.equals(args.get(0), "ALL") || student.getStudentType().name().equals(args.get(0));
            if (student.getStudentDepartmentId() == departmentId && typeMatches
                    && (year == 0 || student.getEntryYear() == year)) {
                student.setStudentRegistrationTime(time);
            }
        }
    }

}
