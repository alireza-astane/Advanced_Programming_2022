package network;
import serverDataBase.models.User;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{


    private User user;
    public String captchaCode;
    public Socket socket;
    public int authToken;
    public RequestHandler requestHandler;
    public Responser responser;

    public ClientHandler(Socket socket, int TOKEN) throws IOException {
        this.socket=socket;
        this.authToken=TOKEN;
        requestHandler = new RequestHandler(this);
        responser = new Responser(this);
    }

    public User getUser() {
        return user;
    }

    @Override
    public void start() {
        responser.sendAuthToken(authToken);
        requestHandler.start();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getAuthToken() {
        return authToken;
    }

    public void setAuthToken(int authToken) {
        this.authToken = authToken;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public Responser getResponser() {
        return responser;
    }

    public void setResponser(Responser responser) {
        this.responser = responser;
    }
}
