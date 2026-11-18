package network;

import com.google.gson.JsonParseException;
import database.DataManager;
import database.models.User;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/** Serves one connected client on its own thread. */
public class ClientHandler extends Thread {
    private static final Logger log = LogManager.getLogger(ClientHandler.class);
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    private final Server server;
    private final Socket socket;
    private final String authToken;
    private final BufferedReader reader;
    private final PrintWriter writer;

    private Integer userId;
    private boolean mustChangePassword;
    private LocalDateTime previousLogin;
    private String captchaCode;

    ClientHandler(Server server, Socket socket, String authToken) throws IOException {
        super("client-" + NEXT_ID.getAndIncrement());
        this.server = server;
        this.socket = socket;
        this.authToken = authToken;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        setDaemon(true);
    }

    String authToken() {
        return authToken;
    }

    public String captchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public LocalDateTime previousLogin() {
        return previousLogin;
    }

    public void logIn(User user, LocalDateTime previous, boolean passwordExpired) {
        userId = user.getId();
        previousLogin = previous;
        mustChangePassword = passwordExpired;
    }

    public void logOut() {
        userId = null;
        mustChangePassword = false;
    }

    public void passwordChanged() {
        mustChangePassword = false;
    }

    @Override
    public void run() {
        send(new Response().put("authToken", authToken));
        try {
            String line;
            while ((line = Protocol.readLine(reader)) != null) {
                Request request = decode(line);
                if (request != null && request.getRequestType() != null && hasValidToken(request)) {
                    answer(request);
                } else {
                    log.warn("Rejected a malformed request on " + getName());
                }
            }
        } catch (IOException e) {
            log.info(getName() + " ended: " + e.getMessage());
        } finally {
            close();
        }
    }

    private static Request decode(String line) {
        try {
            return Protocol.GSON.fromJson(line, Request.class);
        } catch (JsonParseException | IllegalArgumentException e) {
            return null;
        }
    }

    private boolean hasValidToken(Request request) {
        return request.getAuthToken() != null && MessageDigest.isEqual(
                request.getAuthToken().getBytes(StandardCharsets.UTF_8),
                authToken.getBytes(StandardCharsets.UTF_8));
    }

    private void answer(Request request) {
        Response response;
        try {
            response = DataManager.inTransaction(() -> handle(request));
        } catch (Problem e) {
            response = Response.failure(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Could not handle " + request.getRequestType(), e);
            response = Response.failure("Something went wrong on the server.");
        }
        response.setRequestId(request.getRequestId());
        send(response);
    }

    private Response handle(Request request) {
        Routes.Route route = server.routes().get(request.getRequestType());
        if (route == null) {
            throw new Problem("That request is not supported.");
        }
        User user = userId == null ? null : DataManager.session().find(User.class, userId);
        if (route.roles() != null) {
            authorize(request.getRequestType(), route, user);
        }
        Response response = route.handler().handle(new Call(this, user, request.getArgs(), request.getFile()));
        return response == null ? new Response() : response;
    }

    private void authorize(RequestType type, Routes.Route route, User user) {
        if (user == null) {
            throw new Problem("Please log in first.");
        }
        if (mustChangePassword && type != RequestType.CHANGE_PASSWORD && type != RequestType.LOGOUT) {
            throw new Problem("Please change your password first.");
        }
        if (!route.roles().contains(user.getUserType())) {
            throw new Problem("You do not have access to that.");
        }
    }

    synchronized void send(Response response) {
        writer.print(Protocol.GSON.toJson(response));
        writer.print('\n');
        writer.flush();
    }

    void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
            // already closed
        }
        server.remove(this);
    }
}
