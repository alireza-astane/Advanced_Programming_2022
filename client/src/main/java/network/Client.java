package network;

import GuiControllers.Gui;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import logic.enums.UserType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable{

    public Gui controller;
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

    int authToken;

    public FXMLLoader loader;
    Requester requester;
    ResponseHandler responseHandler;

    Socket socket;
    boolean online=false;



    private void connect() throws IOException {
        log.info("connecting to sever...");
        try {
            socket = new Socket("localhost", 8080);
            this.setOnline(true);
            requester = new Requester(this);
            responseHandler = new ResponseHandler(this);
            pinger = new Pinger(this);
            //pinger.start();
            responseHandler.start();
            log.info("connected to server");
        } catch (IOException e) {
            log.info("Connection failed.Offline mode!");
            //go offline mode
        }


        //PrintWriter printWriter = new PrintWriter(sendSocket.getOutputStream());
        //Scanner scanner = new Scanner(sendSocket.getInputStream());

        /*try {
            while (true) {
                String input = scanner.nextLine();
                GetMessage.getInstance(sendSocket, input);
            }
        } catch (NoSuchElementException | InterruptedException e) {
            e.printStackTrace();
        }  */
    }


    public int getAuthToken() {
        return authToken;
    }

    public Requester getRequester() {
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

    public void setAuthToken(int authToken) {
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
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
