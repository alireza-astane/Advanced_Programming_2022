package network;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.DataManager;
import serverDataBase.models.User;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;

public class Server {
    static Logger log = LogManager.getLogger(Server.class);
    public static Server server;
    public ServerSocket serverSocket;
    public static Server getInstance() {
        if (server == null)
            server = new Server();
        return server;
    }
    DatabaseConnectionChecker databaseConnectionChecker;

    HashMap<String,String> usernameAndPassMap;

    public void init() {
        log.info("Server started...");
        DataManager.connect();
        DataManager.load();
        databaseConnectionChecker =  new DatabaseConnectionChecker();
        databaseConnectionChecker.start();
        loadUsernamesAndPasses();
        log.info("server has been initialized");
        try {
            Server.getInstance().serverSocket = new ServerSocket(8080);
            while (true) {
                log.info("Server waiting for new connection");
                Socket socket = serverSocket.accept();
                initClient(socket);
            }

        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    private void loadUsernamesAndPasses() {
        usernameAndPassMap = new HashMap<>();
        for(User user:DataManager.getUsers()){
            usernameAndPassMap.put(user.getUsername(),user.getUserPassword());
        }
    }

    private void initClient(Socket socket) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        log.info("new client accepted");
        int TOKEN = this.authTokenGenerator();
        ClientHandler clientHandler = new ClientHandler(socket,TOKEN);
        clientHandler.start();
    }

    public int authTokenGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
        return secureRandomGenerator.nextInt();
    }
}

