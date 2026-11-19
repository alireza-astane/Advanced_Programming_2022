package ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import static ui.PortalHarness.*;

/** Records frames of a short walk through the portal for the README. Run with the readmeGif task. */
class ReadmeTour {
    private static final int HOLD = 9;
    private File directory;
    private int frame;

    @Test
    void record() throws Exception {
        directory = new File(System.getProperty("portal.frames"));
        directory.mkdirs();
        PortalHarness harness = new PortalHarness();

        PortalApp student = openWindow();
        until(() -> call(() -> find(student, "#username") != null));
        settle();
        hold(student, HOLD);
        type(student, "#username", "student.sara");
        type(student, "#password", "demo1234");
        type(student, "#captcha", CAPTCHA);
        fxRun(() -> ((Button) root(student).lookup("#sign-in")).fire());
        waitForShell(student);
        show(student, "Home");
        show(student, "Courses");
        typeInto(student, searchField(student, "Course name or number"), "data");
        hold(student, HOLD);
        show(student, "Unit selection");
        fxRun(() -> rowButton(student, "Take", "Databases").fire());
        settle();
        hold(student, HOLD + 4);
        show(student, "Weekly plan");
        show(student, "Messenger");
        fxRun(() -> ((ListView<?>) root(student).lookup(".list-view")).getSelectionModel().select(0));
        settle();
        hold(student, 4);
        typeInto(student, searchField(student, "Write a message"), "See you in class tomorrow.");
        fxRun(() -> buttonCalled(student, "Send").fire());
        settle();
        hold(student, HOLD);

        PortalApp professor = signedIn("prof.karimi");
        show(professor, "Grade book");
        show(professor, "My courses");
        PortalApp deputy = signedIn("deputy.cs");
        show(deputy, "Department grades");
        fxRun(deputy::toggleTheme);
        settle();
        hold(deputy, HOLD);
        fxRun(deputy::toggleTheme);
        harness.stopServer();
    }

    private static PortalApp signedIn(String username) throws Exception {
        PortalApp app = openWindow();
        signIn(app, username, "demo1234");
        waitForShell(app);
        settle();
        return app;
    }

    private void show(PortalApp app, String page) throws Exception {
        ToggleButton item = navigationItems(app).stream().filter(button -> button.getText().equals(page))
                .findFirst().orElseThrow();
        fxRun(item::fire);
        settle();
        Thread.sleep(120);
        hold(app, HOLD);
    }

    private void type(PortalApp app, String selector, String text) throws Exception {
        typeInto(app, (TextField) find(app, selector), text);
    }

    private void typeInto(PortalApp app, TextField field, String text) throws Exception {
        for (int i = 1; i <= text.length(); i++) {
            String prefix = text.substring(0, i);
            fxRun(() -> field.setText(prefix));
            if (i % 2 == 0 || i == text.length()) {
                capture(app);
            }
        }
    }

    private static TextField searchField(PortalApp app, String prompt) throws Exception {
        return fx(() -> root(app).lookupAll(".text-field").stream()
                .filter(node -> node instanceof TextField field && prompt.equals(field.getPromptText()))
                .map(TextField.class::cast).findFirst().orElseThrow());
    }

    private static Button buttonCalled(PortalApp app, String text) {
        return root(app).lookupAll(".button").stream()
                .filter(node -> node instanceof Button button && button.getText().equals(text))
                .map(Button.class::cast).findFirst().orElseThrow();
    }

    private static Button rowButton(PortalApp app, String text, String course) {
        return root(app).lookupAll(".link-button").stream()
                .filter(node -> node instanceof Button button && button.getText().equals(text) && inRow(node, course))
                .map(Button.class::cast).findFirst().orElseThrow();
    }

    private static boolean inRow(Node node, String course) {
        for (Node current = node; current != null; current = current.getParent()) {
            if (current instanceof TableRow<?> row && row.getItem() instanceof Map<?, ?> item) {
                return course.equals(item.get("name"));
            }
        }
        return false;
    }

    private static Parent root(PortalApp app) {
        return app.portal().stage().getScene().getRoot();
    }

    private void hold(PortalApp app, int frames) throws Exception {
        File first = capture(app);
        for (int i = 1; i < frames; i++) {
            Files.copy(first.toPath(), next().toPath());
        }
    }

    private File next() {
        return new File(directory, String.format("%04d.png", frame++));
    }

    private File capture(PortalApp app) throws Exception {
        File file = next();
        fxRun(() -> {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(root(app).snapshot(new SnapshotParameters(), null), null), "png", file);
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
        });
        return file;
    }
}
