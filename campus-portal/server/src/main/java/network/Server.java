package network;

import database.DataManager;
import database.DemoData;
import logic.AccountService;
import logic.CatalogService;
import logic.Configuration;
import logic.CoursewareService;
import logic.EducationService;
import logic.MessengerService;
import logic.RegistrarService;
import logic.ScoreService;
import logic.UnitSelectionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final Logger log = LogManager.getLogger(Server.class);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static Server instance;

    private final Routes routes = new Routes();
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private ServerSocket serverSocket;
    private ScheduledExecutorService background;

    private Server() {
        AccountService.register(routes);
        CatalogService.register(routes);
        EducationService.register(routes);
        ScoreService.register(routes);
        UnitSelectionService.register(routes);
        MessengerService.register(routes);
        CoursewareService.register(routes);
        RegistrarService.register(routes);
    }

    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    Routes routes() {
        return routes;
    }

    public void start(int port) throws IOException {
        DataManager.connect();
        DemoData.loadIfEmpty();
        serverSocket = new ServerSocket(port);
        background = Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "server-background");
            thread.setDaemon(true);
            return thread;
        });
        schedule(this::checkDatabase, Configuration.number("databaseCheckSeconds"));
        schedule(this::finishSelections, Configuration.number("selectionCheckSeconds"));
        log.info("Listening on port " + getPort());
    }

    private void schedule(Runnable task, int seconds) {
        background.scheduleWithFixedDelay(task, seconds, seconds, TimeUnit.SECONDS);
    }

    private void finishSelections() {
        try {
            DataManager.inTransaction(UnitSelectionService::finishExpiredSelections);
        } catch (RuntimeException e) {
            log.error("Could not finish unit selections", e);
        }
    }

    private void checkDatabase() {
        if (!DataManager.isConnected()) {
            log.error("The database connection was lost. Disconnecting clients.");
            clients.forEach(client -> client.send(Response.failure("The server lost its database connection.")));
            stop();
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void acceptClients() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, socket, newToken());
                clients.add(client);
                client.start();
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    log.error("Could not accept a client", e);
                }
            }
        }
    }

    void remove(ClientHandler client) {
        clients.remove(client);
    }

    /** The handler that was given this token, for tests that need to read its captcha. */
    ClientHandler clientWithToken(String token) {
        return clients.stream().filter(client -> client.authToken().equals(token)).findFirst().orElseThrow();
    }

    public void stop() {
        if (background != null) {
            background.shutdownNow();
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.warn("Could not close the server socket", e);
        }
        List.copyOf(clients).forEach(ClientHandler::close);
    }

    private static String newToken() {
        byte[] token = new byte[32];
        RANDOM.nextBytes(token);
        return HexFormat.of().formatHex(token);
    }
}
