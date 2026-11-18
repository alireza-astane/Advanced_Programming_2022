package ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ui.PortalHarness.*;

class PortalWindowTest {
    private static PortalHarness harness;

    @BeforeAll
    static void start() throws Exception {
        harness = new PortalHarness();
    }

    @AfterAll
    static void stop() throws Exception {
        harness.stopServer();
    }

    private static PortalApp signedIn(String username) throws Exception {
        PortalApp app = openWindow();
        signIn(app, username, "demo1234");
        waitForShell(app);
        settle();
        return app;
    }

    private static void openPage(PortalApp app, String name) throws Exception {
        ToggleButton item = navigationItems(app).stream().filter(button -> button.getText().equals(name))
                .findFirst().orElseThrow(() -> new AssertionError("No page called " + name));
        fxRun(item::fire);
        settle();
    }

    private static int tableRows(PortalApp app) throws Exception {
        return fx(() -> app.portal().stage().getScene().getRoot().lookupAll(".table-view").stream()
                .mapToInt(table -> ((TableView<?>) table).getItems().size()).max().orElse(0));
    }

    private static void assertHealthy(PortalApp app, String context) throws Exception {
        assertEquals("", fx(() -> app.portal().shell().failure()), context);
        assertTrue(UNCAUGHT.isEmpty(), context + ": " + UNCAUGHT);
    }

    @Test
    void wrongPasswordStaysOnTheLoginScreen() throws Exception {
        PortalApp app = openWindow();
        signIn(app, "student.sara", "wrong-password");
        until(() -> call(() -> fx(() -> "Wrong username or password.".equals(
                ((Label) app.portal().stage().getScene().getRoot().lookup("#login-message")).getText()))));
        assertNull(fx(() -> app.portal().shell()));
    }

    @Test
    void everyPageOpensForEveryRole() throws Exception {
        Map<String, Integer> expectedPages = Map.of("student.sara", 14, "student.reza", 13, "prof.karimi", 11,
                "head.cs", 11, "deputy.cs", 14, "registrar", 5, "admin", 4);
        for (var entry : expectedPages.entrySet()) {
            PortalApp app = signedIn(entry.getKey());
            List<ToggleButton> items = navigationItems(app);
            assertEquals(entry.getValue(), items.size(), entry.getKey() + " " + items.stream().map(ToggleButton::getText).toList());
            for (ToggleButton item : items) {
                fxRun(item::fire);
                settle();
                assertHealthy(app, entry.getKey() + " on " + item.getText());
            }
        }
    }

    @Test
    void pagesShowTheirData() throws Exception {
        PortalApp student = signedIn("student.sara");
        openPage(student, "Courses");
        assertTrue(tableRows(student) >= 10);
        openPage(student, "Exams");
        assertEquals(2, tableRows(student));
        openPage(student, "Unit selection");
        assertTrue(tableRows(student) >= 4);
        openPage(student, "My courses");
        assertEquals(2, tableRows(student));
    }

    @Test
    void studentsWithoutAWindowDoNotSeeUnitSelection() throws Exception {
        PortalApp app = signedIn("student.omid");
        assertTrue(navigationItems(app).stream().noneMatch(item -> item.getText().equals("Unit selection")));
    }

    @Test
    void offlineModeShowsSavedPagesThenReturnsToLogin() throws Exception {
        PortalApp app = signedIn("student.lena");
        openPage(app, "Exams");
        int exams = tableRows(app);
        openPage(app, "Home");
        harness.stopServer();
        until(() -> call(() -> fx(() -> app.portal().shell().isOfflineBannerShown())));
        assertTrue(fx(() -> !app.portal().isOnline()));
        openPage(app, "Exams");
        assertEquals(exams, tableRows(app));
        openPage(app, "Courses");
        assertNotNull(fx(() -> app.portal().stage().getScene().getRoot().lookupAll(".muted").stream()
                .filter(node -> node instanceof Label label && label.getText().contains("not available offline"))
                .findFirst().orElse(null)));
        fxRun(() -> app.portal().messageAdministrator("Saved while offline"));
        fxRun(() -> ((Button) app.portal().stage().getScene().getRoot().lookup("#reconnect")).fire());
        settle();
        assertTrue(fx(() -> app.portal().shell().failure().contains("cannot be reached")));

        harness.startServer();
        fxRun(() -> ((Button) app.portal().stage().getScene().getRoot().lookup("#reconnect")).fire());
        until(() -> call(() -> fx(() -> app.portal().shell() == null)));
        signIn(app, "student.lena", "demo1234");
        waitForShell(app);
        settle();
        PortalApp admin = signedIn("admin");
        openPage(admin, "Messenger");
        assertNotNull(fx(() -> admin.portal().stage().getScene().getRoot().lookupAll(".label").stream()
                .filter(node -> node instanceof Label label && label.getText().equals("Saved while offline"))
                .findFirst().orElse(null)));
    }
}
