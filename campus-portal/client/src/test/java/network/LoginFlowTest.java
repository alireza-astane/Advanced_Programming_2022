package network;

import GuiControllers.Gui;
import GuiControllers.LoginGui;
import GuiControllers.StudentMenuGui;
import javafx.application.Platform;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs the real server in its own process and drives the login screen against it. */
class LoginFlowTest {
    private static final String CAPTCHA = "1234";
    private static Process server;

    @BeforeAll
    static void start() throws Exception {
        int port;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        server = new ProcessBuilder(List.of(java, "-Djava.awt.headless=true", "-Dportal.port=" + port,
                "-Dportal.db.url=jdbc:h2:mem:client-test;DB_CLOSE_DELAY=-1", "-Dportal.test.captcha=" + CAPTCHA,
                "-cp", System.getProperty("server.classpath"), "ServerMain"))
                .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
        waitFor(() -> canConnect(port));

        System.setProperty("portal.port", String.valueOf(port));
        Configuration.load();
        Platform.startup(() -> { });
        Platform.setImplicitExit(false);
        Client.getInstance().run();
        waitFor(() -> Client.getInstance().authToken != null);
    }

    @AfterAll
    static void stop() {
        server.destroyForcibly();
    }

    private static boolean canConnect(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static void waitFor(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 30000;
        while (!condition.getAsBoolean()) {
            assertTrue(System.currentTimeMillis() < deadline, "timed out");
            Thread.sleep(25);
        }
    }

    private static <T> T fx(Callable<T> action) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                result.complete(action.call());
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });
        return result.get(15, TimeUnit.SECONDS);
    }

    @Test
    void wrongPasswordStaysOnLoginAndCorrectPasswordOpensTheMenu() throws Exception {
        Stage stage = fx(() -> {
            Stage window = new Stage();
            Gui.setStage(window, FxmlFiles.LOGIN);
            return window;
        });
        LoginGui login = assertInstanceOf(LoginGui.class, Client.getInstance().controller);
        fx(() -> {
            login.NameField.setText("student.sara");
            login.PassField.setText("wrong-password");
            login.CaptchaField.setText(CAPTCHA);
            login.login(null);
            return null;
        });
        waitFor(() -> "Wrong username or password".equals(text(login)));

        fx(() -> {
            login.PassField.setText("demo1234");
            login.login(null);
            return null;
        });
        waitFor(() -> Client.getInstance().controller instanceof StudentMenuGui);
        assertEquals(true, fx(stage::isShowing));
    }

    private static String text(LoginGui login) {
        try {
            return fx(login.ErrorLabel::getText);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
