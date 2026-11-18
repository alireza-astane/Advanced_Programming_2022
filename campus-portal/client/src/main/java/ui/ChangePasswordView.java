package ui;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import network.Request;
import network.RequestType;

/** Shown when the password has expired. There is no way past it except changing the password. */
final class ChangePasswordView {
    private final Portal portal;
    private final PortalApp app;

    ChangePasswordView(Portal portal, PortalApp app) {
        this.portal = portal;
        this.app = app;
    }

    Parent view() {
        Label title = new Label("Choose a new password");
        title.getStyleClass().add("brand-small");
        Label explanation = new Label("It has been more than three hours since your last visit.");
        explanation.setWrapText(true);
        explanation.getStyleClass().add("muted");
        PasswordField current = field("Current password");
        PasswordField next = field("New password");
        PasswordField repeat = field("Repeat the new password");
        Label message = new Label();
        message.getStyleClass().add("error");
        message.setWrapText(true);
        Button save = new Button("Change password");
        save.getStyleClass().add("primary");
        save.setDefaultButton(true);
        save.setMaxWidth(Double.MAX_VALUE);
        save.setOnAction(event -> portal.request(
                new Request(RequestType.CHANGE_PASSWORD, current.getText(), next.getText(), repeat.getText()),
                response -> {
                    portal.signedIn(response);
                    app.showShell();
                }, message::setText));
        Button signOut = new Button("Sign out");
        signOut.setMaxWidth(Double.MAX_VALUE);
        signOut.setOnAction(event -> portal.logout());
        VBox card = new VBox(title, explanation, current, next, repeat, save, signOut, message);
        card.getStyleClass().add("login-card");
        return new StackPane(card);
    }

    private static PasswordField field(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        return field;
    }
}
