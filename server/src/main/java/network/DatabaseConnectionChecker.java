package network;

import serverDataBase.DataManager;

public class DatabaseConnectionChecker extends Thread {
    private static final long CHECK_MILLIS = 5000;

    private final Server server;

    DatabaseConnectionChecker(Server server) {
        super("database-checker");
        this.server = server;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Thread.sleep(CHECK_MILLIS);
                if (!DataManager.isConnected()) {
                    server.databaseLost();
                    return;
                }
            }
        } catch (InterruptedException ignored) {
            // the server is shutting down
        }
    }
}
