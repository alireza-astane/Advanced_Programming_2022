import logic.Configuration;
import network.Protocol;
import network.Server;
import org.hibernate.HibernateException;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Configuration.load();
        int port = Integer.parseInt(Configuration.setting("portal.port", "PORTAL_PORT",
                String.valueOf(Protocol.DEFAULT_PORT)));
        Server server = Server.getInstance();
        try {
            server.start(port);
        } catch (HibernateException e) {
            System.err.println("The server cannot run without its database: " + e.getMessage());
            System.exit(1);
        }
        server.acceptClients();
    }
}
