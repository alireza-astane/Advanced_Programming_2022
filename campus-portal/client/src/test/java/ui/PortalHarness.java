package ui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the real server in its own process and drives the real client window against it. */
final class PortalHarness {
    static final String CAPTCHA = "1234";
    static final Queue<Throwable> UNCAUGHT = new ConcurrentLinkedQueue<>();
    private static boolean toolkitStarted;

    private final int port;
    private Process server;

    PortalHarness() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        startToolkit();
        System.setProperty("portal.port", String.valueOf(port));
        System.setProperty("portal.data", Files.createTempDirectory("portal-client").toString());
        startServer();
    }

    private static synchronized void startToolkit() {
        if (!toolkitStarted) {
            Thread.setDefaultUncaughtExceptionHandler((thread, error) -> UNCAUGHT.add(error));
            Platform.startup(() -> Thread.currentThread().setUncaughtExceptionHandler((thread, error) -> UNCAUGHT.add(error)));
            Platform.setImplicitExit(false);
            toolkitStarted = true;
        }
    }

    void startServer() throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        server = new ProcessBuilder(List.of(java, "-Djava.awt.headless=true", "-Dportal.port=" + port,
                "-Dportal.db.url=jdbc:h2:mem:client-test;DB_CLOSE_DELAY=-1", "-Dportal.test.captcha=" + CAPTCHA,
                "-cp", System.getProperty("server.classpath"), "ServerMain"))
                .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
        until(this::serverAccepts);
    }

    void stopServer() throws InterruptedException {
        server.destroyForcibly();
        server.waitFor();
    }

    private boolean serverAccepts() {
        try (Socket ignored = new Socket("localhost", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static void until(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 30000;
        while (!condition.getAsBoolean()) {
            assertTrue(System.currentTimeMillis() < deadline, "timed out");
            Thread.sleep(20);
        }
    }

    static <T> T fx(Callable<T> action) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                result.complete(action.call());
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });
        return result.get(20, TimeUnit.SECONDS);
    }

    static void fxRun(Runnable action) throws Exception {
        fx(() -> {
            action.run();
            return null;
        });
    }

    /** Waits until everything already queued on the interface thread and network has finished. */
    static void settle() throws Exception {
        for (int i = 0; i < 6; i++) {
            Thread.sleep(40);
            fxRun(() -> { });
        }
    }

    static PortalApp openWindow() throws Exception {
        return fx(() -> {
            PortalApp app = new PortalApp();
            Stage stage = new Stage();
            app.open(stage);
            stage.show();
            return app;
        });
    }

    static Node find(PortalApp app, String selector) throws Exception {
        return fx(() -> app.portal().stage().getScene().getRoot().lookup(selector));
    }

    static void signIn(PortalApp app, String username, String password) throws Exception {
        until(() -> call(() -> find(app, "#username") != null));
        settle();
        fxRun(() -> {
            var root = app.portal().stage().getScene().getRoot();
            ((TextInputControl) root.lookup("#username")).setText(username);
            ((TextInputControl) root.lookup("#password")).setText(password);
            ((TextInputControl) root.lookup("#captcha")).setText(CAPTCHA);
            ((Button) root.lookup("#sign-in")).fire();
        });
    }

    static boolean call(Callable<Boolean> check) {
        try {
            return check.call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static void waitForShell(PortalApp app) throws InterruptedException {
        until(() -> call(() -> fx(() -> app.portal().shell() != null)));
    }

    static List<ToggleButton> navigationItems(PortalApp app) throws Exception {
        return fx(() -> {
            List<ToggleButton> items = new ArrayList<>();
            for (Node node : app.portal().stage().getScene().getRoot().lookupAll(".nav-item")) {
                if (node instanceof ToggleButton item && item.isManaged()) {
                    items.add(item);
                }
            }
            return items;
        });
    }
}
