package network;

import serverDataBase.DataManager;
import serverDataBase.models.User;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class ClientHandler {
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    private final long id = NEXT_ID.incrementAndGet();
    private Integer userId;
    private boolean mustChangePassword;
    public String captchaCode;
    public final Socket socket;
    public final String authToken;
    public final RequestHandler requestHandler;
    public final Responser responser;

    public ClientHandler(Socket socket, String authToken) throws IOException {
        this.socket = socket;
        this.authToken = authToken;
        requestHandler = new RequestHandler(this);
        responser = new Responser(this);
    }

    public long getId() {
        return id;
    }

    /** The logged in user, read inside the current request's transaction. */
    public User getUser() {
        return userId == null ? null : DataManager.getUserById(userId);
    }

    public boolean isLoggedIn() {
        return userId != null;
    }

    public void setUser(User user) {
        userId = user == null ? null : user.getId();
    }

    public boolean mustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public void start() {
        responser.sendAuthToken(authToken);
        requestHandler.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
            // already closed
        }
        Server.getInstance().removeClient(this);
    }
}
