package ui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PortalApp extends Application {
    private Portal portal;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        open(stage);
        stage.setTitle("Campus Portal");
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.show();
    }

    /** Builds the window's content, so tests can open it without launching the toolkit twice. */
    public void open(Stage stage) {
        portal = new Portal(this, stage);
        scene = new Scene(new LoginView(portal, this, "").view(), 1320, 780);
        scene.getStylesheets().add(PortalApp.class.getResource("/ui/portal.css").toExternalForm());
        stage.setScene(scene);
        applyTheme();
    }

    public Portal portal() {
        return portal;
    }

    private void setContent(Parent content) {
        scene.setRoot(content);
        applyTheme();
    }

    void applyTheme() {
        boolean dark = "dark".equals(portal.store().setting("theme", "light"));
        scene.getRoot().getStyleClass().remove("dark");
        if (dark) {
            scene.getRoot().getStyleClass().add("dark");
        }
    }

    void toggleTheme() {
        boolean dark = "dark".equals(portal.store().setting("theme", "light"));
        portal.store().setSetting("theme", dark ? "light" : "dark");
        applyTheme();
    }

    void showLogin(String message) {
        portal.setShell(null);
        setContent(new LoginView(portal, this, message).view());
    }

    void showChangePassword() {
        setContent(new ChangePasswordView(portal, this).view());
    }

    void showShell() {
        Shell shell = new Shell(portal, this);
        portal.setShell(shell);
        setContent(shell.view());
        shell.showHome();
    }
}
