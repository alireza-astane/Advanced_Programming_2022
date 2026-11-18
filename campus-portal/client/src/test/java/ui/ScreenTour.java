package ui;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ui.PortalHarness.*;

/** Saves a picture of the main screens. Run with the {@code screenshots} Gradle task. */
class ScreenTour {
    private static final Map<String, Set<String>> TOUR = Map.of(
            "student.sara", Set.of("Home", "Courses", "Unit selection", "Weekly plan", "Requests", "Academic record", "Messenger", "Profile"),
            "prof.karimi", Set.of("Grade book", "Inbox", "My courses"),
            "deputy.cs", Set.of("Department grades", "Selection windows", "Professors"),
            "registrar", Set.of("Student search"));

    @Test
    void capture() throws Exception {
        File directory = new File(System.getProperty("portal.screenshots"));
        directory.mkdirs();
        PortalHarness harness = new PortalHarness();
        PortalApp login = openWindow();
        until(() -> call(() -> find(login, "#username") != null));
        settle();
        save(login, new File(directory, "login.png"));
        for (var person : TOUR.entrySet()) {
            PortalApp app = openWindow();
            signIn(app, person.getKey(), "demo1234");
            waitForShell(app);
            settle();
            List<ToggleButton> items = navigationItems(app);
            for (ToggleButton item : items) {
                if (person.getValue().contains(item.getText())) {
                    fxRun(item::fire);
                    settle();
                    Thread.sleep(150);
                    save(app, new File(directory, person.getKey().replace('.', '-') + "-"
                            + item.getText().toLowerCase().replace(' ', '-') + ".png"));
                }
            }
        }
        PortalApp dark = openWindow();
        signIn(dark, "student.sara", "demo1234");
        waitForShell(dark);
        fxRun(dark::toggleTheme);
        settle();
        save(dark, new File(directory, "dark-home.png"));
        fxRun(dark::toggleTheme);
        harness.stopServer();
    }

    private static void save(PortalApp app, File file) throws Exception {
        fxRun(() -> {
            try {
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setTransform(new Scale(1.5, 1.5));
                WritableImage image = app.portal().stage().getScene().getRoot().snapshot(parameters, null);
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
        });
    }
}
