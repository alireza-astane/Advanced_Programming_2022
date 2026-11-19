package ui;

import logic.Configuration;
import network.Server;

import java.nio.file.Path;

/** Runs the portal server inside the client, with demo data, for people trying it on one computer. */
final class DemoServer {
    private static boolean running;

    private DemoServer() {
    }

    static synchronized boolean isRunning() {
        return running;
    }

    /** Starts the server on a free port and points the client at it. Returns a problem, or null. */
    static synchronized String start() {
        if (running) {
            return null;
        }
        Path data = Path.of(System.getProperty("portal.data", System.getProperty("user.home") + "/.campus-portal"));
        System.setProperty("portal.db.url", "jdbc:h2:file:" + data.resolve("demo-server").resolve("campus")
                .toAbsolutePath() + ";DB_CLOSE_ON_EXIT=TRUE");
        try {
            Configuration.load();
            Server server = Server.getInstance();
            server.start(0);
            Thread acceptor = new Thread(server::acceptClients, "demo-server");
            acceptor.setDaemon(true);
            acceptor.start();
            System.setProperty("portal.host", "localhost");
            System.setProperty("portal.port", String.valueOf(server.getPort()));
            running = true;
            return null;
        } catch (Exception e) {
            return "The demo server could not start: " + e.getMessage();
        }
    }
}
