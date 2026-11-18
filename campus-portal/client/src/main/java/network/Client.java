package network;

import GuiControllers.Gui;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import logic.Configuration;
import javafx.scene.Scene;
import logic.enums.UserType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Client implements Runnable{
    private static final int CONNECT_TIMEOUT_MILLIS = 3000;

    Consumer<String> notifier = Client::showAlert;

    public volatile Gui controller;
    Pinger pinger;


    public Pinger getPinger() {
        return pinger;
    }

    public void setPinger(Pinger pinger) {
        this.pinger = pinger;
    }

    static Logger log = LogManager.getLogger(Client.class);
    public Scene scene;
    public UserType userType;

    String pageData;

    public void setPageData(String pageData) {
        this.pageData = pageData;
    }

    public String getPageData() {
        return pageData;
    }

    public static Client client;
    public static Client getInstance(){
        if (client == null)
            client = new Client();
        return client;

    }

    volatile String authToken;

    public FXMLLoader loader;
    Requester requester;
    ResponseHandler responseHandler;

    Socket socket;
    volatile boolean online=false;



    /** Opens a new connection, replacing any old one, and reports whether it worked. */
    public synchronized boolean connect() {
        closeSocket();
        authToken = null;
        online = false;
        String host = Configuration.setting("portal.host", "PORTAL_HOST", "localhost");
        int port = port();
        log.info("Connecting to " + host + ":" + port);
        try {
            Socket opened = new Socket();
            opened.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MILLIS);
            socket = opened;
            online = true;
            requester = new Requester(this);
            responseHandler = new ResponseHandler(this);
            pinger = new Pinger(this);
            responseHandler.start();
            log.info("Connected to the server");
            return true;
        } catch (IOException e) {
            closeSocket();
            online = false;
            log.info("Could not reach the server: " + e.getMessage());
            return false;
        }
    }

    private static int port() {
        return Integer.parseInt(Configuration.setting("portal.port", "PORTAL_PORT",
                String.valueOf(Protocol.DEFAULT_PORT)));
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
                // it is being replaced anyway
            }
            socket = null;
        }
    }

    /** Called when a connection ends. A connection that was already replaced is ignored. */
    synchronized void connectionLost(Socket lost) {
        if (lost == socket) {
            online = false;
            notifier.accept("The connection to the server was lost.");
        }
    }

    void reportUnreachable() {
        notifier.accept("Cannot reach the portal server at "
                + Configuration.setting("portal.host", "PORTAL_HOST", "localhost") + ":" + port()
                + ". Start the server, then try again.");
    }

    private static void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.setHeaderText(null);
            alert.show();
        });
    }

    public String getAuthToken() {
        return authToken;
    }

    public synchronized Requester getRequester() {
        if (requester == null) {
            requester = new Requester(this);
        }
        return requester;
    }

    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isOnline() {
        return online;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        connect();
    }


}
