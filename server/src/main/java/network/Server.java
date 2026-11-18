package network;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.DataManager;
import serverDataBase.DemoData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    static Logger log = LogManager.getLogger(Server.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static Server server;

    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private ServerSocket serverSocket;
    private DatabaseConnectionChecker databaseConnectionChecker;

    public static synchronized Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public void start(int port) throws IOException {
        log.info("Server started");
        DataManager.connect();
        DemoData.loadIfEmpty();
        databaseConnectionChecker = new DatabaseConnectionChecker(this);
        databaseConnectionChecker.start();
        serverSocket = new ServerSocket(port);
        log.info("Listening on port " + serverSocket.getLocalPort());
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void acceptClients() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, authTokenGenerator());
                clients.add(clientHandler);
                clientHandler.start();
                log.info("Client " + clientHandler.getId() + " connected");
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    log.error("Could not accept a client", e);
                }
            }
        }
    }

    void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    Set<ClientHandler> clients() {
        return clients;
    }

    public void stop() {
        try {
            if (databaseConnectionChecker != null) {
                databaseConnectionChecker.interrupt();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.warn("Could not close the server socket", e);
        }
        clients.forEach(ClientHandler::close);
    }

    /** Tells every client the database is gone. Clients then fall back to offline mode. */
    void databaseLost() {
        log.error("Database connection lost. Disconnecting clients.");
        clients.forEach(client -> client.responser.sendError("The server lost its database connection."));
        stop();
    }

    public static String authTokenGenerator() {
        byte[] token = new byte[32];
        RANDOM.nextBytes(token);
        return HexFormat.of().formatHex(token);
    }
}
