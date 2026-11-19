package ui;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.enums.UserType;
import network.Connection;
import network.LocalStore;
import network.Protocol;
import network.Request;
import network.RequestType;
import network.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/** The running application: its connection, the signed-in user and the current window. */
public final class Portal {
    private final Stage stage;
    private final PortalApp app;
    private final LocalStore store = new LocalStore();
    private Connection connection;
    private Shell shell;

    private String userId;
    private String fullName = "";
    private UserType role;
    private String masterType = "";
    private String studentType = "";
    private String sessionKey = "";

    Portal(PortalApp app, Stage stage) {
        this.app = app;
        this.stage = stage;
    }

    public Stage stage() {
        return stage;
    }

    public LocalStore store() {
        return store;
    }

    public String fullName() {
        return fullName;
    }

    public UserType role() {
        return role;
    }

    public String studentType() {
        return studentType;
    }

    public boolean isHead() {
        return "HEAD".equals(masterType);
    }

    public boolean isDeputy() {
        return role == UserType.ASSISTANT;
    }

    public boolean isStudent() {
        return role == UserType.STUDENT;
    }

    public boolean isProfessor() {
        return role == UserType.MASTER || role == UserType.ASSISTANT;
    }

    public boolean isOnline() {
        return connection != null && connection.isOpen();
    }

    Shell shell() {
        return shell;
    }

    public void setShell(Shell shell) {
        if (this.shell != null && this.shell != shell) {
            this.shell.stop();
        }
        this.shell = shell;
    }

    private static String setting(String property, String environment, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null) {
            value = System.getenv(environment);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }

    String serverAddress() {
        return setting("portal.host", "PORTAL_HOST", "localhost") + ":" + port();
    }

    private static int port() {
        return Integer.parseInt(setting("portal.port", "PORTAL_PORT", String.valueOf(Protocol.DEFAULT_PORT)));
    }

    boolean connect() {
        if (isOnline()) {
            return true;
        }
        try {
            connection = Connection.open(setting("portal.host", "PORTAL_HOST", "localhost"), port(),
                    message -> Platform.runLater(() -> showError(message)),
                    () -> Platform.runLater(this::connectionLost));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void connectionLost() {
        if (shell != null) {
            shell.setOnline(false);
            shell.showError("The connection to the server was lost.");
        } else {
            app.showLogin("The connection to the server was lost.");
        }
    }

    public void reconnect() {
        if (!connect()) {
            showError("The server still cannot be reached.");
            return;
        }
        request(new Request(RequestType.RESUME, sessionKey), response -> {
            shell.setOnline(true);
            sendQueuedMessages();
            shell.refreshPage();
            showMessage("Back online.");
        }, error -> app.showLogin("Please log in again."));
    }

    void signedIn(Response response) {
        userId = response.get("userId");
        fullName = response.get("fullName");
        role = UserType.valueOf(response.get("role"));
        masterType = response.get("masterType");
        studentType = response.get("studentType");
        if (!response.get("sessionKey").isEmpty()) {
            sessionKey = response.get("sessionKey");
            rememberUser();
            sendQueuedMessages();
        }
    }

    private void rememberUser() {
        store.setSetting("lastUserId", userId);
        store.setSetting("lastUserName", fullName);
        store.setSetting("lastUserRole", role.name());
        store.setSetting("lastMasterType", masterType);
        store.setSetting("lastStudentType", studentType);
    }

    boolean hasSavedUser() {
        return !store.setting("lastUserId", "").isEmpty();
    }

    String savedUserName() {
        return store.setting("lastUserName", "");
    }

    void useSavedUser() {
        userId = store.setting("lastUserId", "");
        fullName = store.setting("lastUserName", "");
        role = UserType.valueOf(store.setting("lastUserRole", "STUDENT"));
        masterType = store.setting("lastMasterType", "");
        studentType = store.setting("lastStudentType", "");
    }

    public void logout() {
        if (isOnline()) {
            connection.send(new Request(RequestType.LOGOUT, sessionKey));
        }
        sessionKey = "";
        app.showLogin("");
    }

    /** Sends a request and calls back on the interface thread. */
    public void request(Request request, Consumer<Response> onSuccess, Consumer<String> onError) {
        CompletableFuture<Response> future = isOnline() ? connection.send(request)
                : CompletableFuture.failedFuture(new IOException("offline"));
        future.whenComplete((response, failure) -> Platform.runLater(() -> {
            if (failure != null) {
                onError.accept("You are offline.");
            } else if (response.isOk()) {
                onSuccess.accept(response);
            } else {
                onError.accept(response.getError());
            }
        }));
    }

    /** Loads a page, from the server when possible and otherwise from its saved copy. */
    public void load(Request request, Consumer<Response> onSuccess) {
        if (!isOnline()) {
            loadSaved(request, onSuccess);
            return;
        }
        request(request, response -> {
            if (userId != null) {
                store.savePage(userId, request, Protocol.GSON.fromJson(Protocol.GSON.toJson(response), Response.class));
            }
            onSuccess.accept(response);
        }, error -> {
            if (isOnline()) {
                showError(error);
            } else {
                loadSaved(request, onSuccess);
            }
        });
    }

    private void loadSaved(Request request, Consumer<Response> onSuccess) {
        Response saved = store.savedPage(userId, request);
        if (saved != null) {
            onSuccess.accept(saved);
        } else if (shell != null) {
            shell.showUnavailable();
        }
    }

    public void load(RequestType type, Consumer<Response> onSuccess, String... args) {
        load(new Request(type, args), onSuccess);
    }

    /** Carries out a change, then reports it and runs {@code after}. */
    public void act(Request request, String success, Consumer<Response> after) {
        request(request, response -> {
            String message = response.get("message").isEmpty() ? success : response.get("message");
            if (!message.isEmpty()) {
                showMessage(message);
            }
            after.accept(response);
            if (shell != null) {
                shell.refreshPage();
            }
        }, this::showError);
    }

    public void act(RequestType type, String success, String... args) {
        act(new Request(type, args), success, response -> { });
    }

    public void showMessage(String message) {
        if (shell != null) {
            shell.showMessage(message);
        }
    }

    public void showError(String message) {
        if (shell != null) {
            shell.showError(message);
        }
    }

    /** Asks the server for a file and lets the user choose where to save it. */
    public void download(RequestType type, String id) {
        request(new Request(type, id), response -> {
            if (response.getFile() == null) {
                showError("There is no file to download.");
                return;
            }
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(response.get("fileName"));
            var target = chooser.showSaveDialog(stage);
            if (target != null) {
                writeFile(target.toPath(), response.getFile());
            }
        }, this::showError);
    }

    private void writeFile(java.nio.file.Path target, byte[] bytes) {
        try {
            Files.write(target, bytes);
            showMessage("Saved " + target.getFileName() + ".");
        } catch (IOException e) {
            showError("The file could not be saved.");
        }
    }

    /** Sends a support message now, or keeps it until the connection returns. */
    public void messageAdministrator(String text) {
        if (isOnline()) {
            act(RequestType.MESSAGE_ADMIN, "Your message was sent to the administrator.", text);
            return;
        }
        List<String> queue = store.queuedMessages(userId);
        queue.add(text);
        store.setQueuedMessages(userId, queue);
        showMessage("You are offline. The message will be sent when you reconnect.");
    }

    private void sendQueuedMessages() {
        List<String> queue = store.queuedMessages(userId);
        if (!queue.isEmpty()) {
            queue.forEach(text -> connection.send(new Request(RequestType.MESSAGE_ADMIN, text)));
            store.setQueuedMessages(userId, List.of());
        }
    }
}
