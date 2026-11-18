import logic.Configuration;
import network.Protocol;
import network.Server;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Configuration.load();
        int port = Integer.parseInt(Configuration.setting("portal.port", "PORTAL_PORT",
                String.valueOf(Protocol.DEFAULT_PORT)));
        Server.getInstance().start(port);
        Server.getInstance().acceptClients();
    }
}
