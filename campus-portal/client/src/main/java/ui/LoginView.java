package ui;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import network.Request;
import network.RequestType;
import network.Response;

import java.io.ByteArrayInputStream;

final class LoginView {
    private final Portal portal;
    private final PortalApp app;
    final TextField username = new TextField();
    final PasswordField password = new PasswordField();
    final TextField captcha = new TextField();
    final Button signIn = new Button("Sign in");
    final Label message = new Label();
    private final ImageView captchaImage = new ImageView();
    private final VBox offlineChoices = new VBox(6);

    LoginView(Portal portal, PortalApp app, String initialMessage) {
        this.portal = portal;
        this.app = app;
        message.setText(initialMessage);
    }

    Parent view() {
        Label title = new Label("Campus Portal");
        title.getStyleClass().add("brand");
        Label subtitle = new Label("Sign in with your university account");
        subtitle.getStyleClass().add("muted");
        username.setId("username");
        password.setId("password");
        captcha.setId("captcha");
        signIn.setId("sign-in");
        message.setId("login-message");
        username.setPromptText("Username");
        password.setPromptText("Password");
        captcha.setPromptText("Code from the picture");
        message.getStyleClass().add("error");
        message.setWrapText(true);
        message.managedProperty().bind(message.textProperty().isNotEmpty());
        offlineChoices.managedProperty().bind(javafx.beans.binding.Bindings.isNotEmpty(offlineChoices.getChildren()));
        signIn.getStyleClass().add("primary");
        signIn.setDefaultButton(true);
        signIn.setMaxWidth(Double.MAX_VALUE);
        signIn.setOnAction(event -> submit());
        Hyperlink newCode = new Hyperlink("New code");
        newCode.setOnAction(event -> loadCaptcha());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox captchaRow = new HBox(8, captchaImage, spacer, newCode);
        captchaRow.setAlignment(Pos.CENTER_LEFT);
        captchaRow.setMinHeight(54);
        VBox card = new VBox(title, subtitle, username, password, captchaRow, captcha, signIn, message, offlineChoices);
        card.getStyleClass().add("login-card");
        loadCaptcha();
        return new StackPane(card);
    }

    private void loadCaptcha() {
        offlineChoices.getChildren().clear();
        if (!portal.connect()) {
            serverUnavailable();
            return;
        }
        portal.request(new Request(RequestType.CAPTCHA), response -> {
            captchaImage.setImage(new Image(new ByteArrayInputStream(response.getFile())));
            captcha.clear();
        }, message::setText);
    }

    private void serverUnavailable() {
        message.setText("Cannot reach the server at " + portal.serverAddress() + ". Start it, then try again.");
        captchaImage.setImage(null);
        Button retry = new Button("Try again");
        retry.setMaxWidth(Double.MAX_VALUE);
        retry.setOnAction(event -> {
            message.setText("");
            loadCaptcha();
        });
        offlineChoices.getChildren().add(retry);
        if (portal.hasSavedUser()) {
            Button saved = new Button("Open saved pages for " + portal.savedUserName());
            saved.setMaxWidth(Double.MAX_VALUE);
            saved.setOnAction(event -> {
                portal.useSavedUser();
                app.showShell();
            });
            offlineChoices.getChildren().add(saved);
        }
    }

    void submit() {
        if (!portal.isOnline()) {
            loadCaptcha();
            return;
        }
        message.setText("");
        signIn.setDisable(true);
        portal.request(new Request(RequestType.LOGIN, username.getText().trim(), password.getText(), captcha.getText().trim()),
                this::signedIn, error -> {
                    signIn.setDisable(false);
                    message.setText(error);
                    password.clear();
                    loadCaptcha();
                });
    }

    private void signedIn(Response response) {
        portal.signedIn(response);
        if (Boolean.parseBoolean(response.get("mustChangePassword"))) {
            app.showChangePassword();
        } else {
            app.showShell();
        }
    }
}
