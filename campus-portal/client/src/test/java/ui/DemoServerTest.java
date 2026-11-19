package ui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import org.junit.jupiter.api.Test;

import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ui.PortalHarness.*;

/** Runs in its own JVM, because the demo server lives inside the client process. */
class DemoServerTest {

    @Test
    void theClientCanStartItsOwnServerAndSignIn() throws Exception {
        int unused;
        try (ServerSocket socket = new ServerSocket(0)) {
            unused = socket.getLocalPort();
        }
        PortalHarness.startToolkitOnly();
        System.setProperty("portal.port", String.valueOf(unused));
        System.setProperty("portal.data", java.nio.file.Files.createTempDirectory("portal-demo").toString());
        System.setProperty("portal.test.captcha", CAPTCHA);
        PortalApp app = openWindow();
        until(() -> call(() -> fx(() -> demoButton(app) != null)));
        fxRun(() -> demoButton(app).fire());
        until(() -> call(() -> fx(() -> DemoServer.isRunning())));
        signIn(app, "student.sara", "demo1234");
        waitForShell(app);
        assertNotNull(fx(() -> app.portal().shell()));
    }

    private static Button demoButton(PortalApp app) {
        for (Node node : app.portal().stage().getScene().getRoot().lookupAll(".button")) {
            if (node instanceof Button button && button.getText().startsWith("Start a demo server")) {
                return button;
            }
        }
        return null;
    }
}
