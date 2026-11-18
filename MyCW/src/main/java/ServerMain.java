import logic.Configuration;
import network.Server;

public class ServerMain {
    public static void main(String[] args) {

        Configuration.load();
        Server server = Server.getInstance();
        server.init();
    }
}