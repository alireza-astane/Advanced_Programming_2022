package ui.pages;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import network.RequestType;
import network.Response;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

final class HomePage extends Page {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("EEEE, MMMM d  HH:mm:ss", Locale.ENGLISH);
    private final Label clock = new Label();
    private final StackPane photo = new StackPane();
    private final VBox details = new VBox(8);

    HomePage(Portal portal) {
        super(portal);
    }

    @Override
    public String title() {
        return "Welcome, " + portal.fullName();
    }

    @Override
    public Node view() {
        Timeline ticking = new Timeline(new KeyFrame(Duration.seconds(1), event -> tick()));
        ticking.setCycleCount(Timeline.INDEFINITE);
        ticking.play();
        clock.sceneProperty().addListener((observable, before, scene) -> {
            if (scene == null) {
                ticking.stop();
            }
        });
        tick();
        clock.getStyleClass().add("muted");
        HBox summary = new HBox(20, photo, details);
        summary.getStyleClass().add("panel");
        return Ui.body(clock, summary);
    }

    private void tick() {
        clock.setText(LocalDateTime.now().format(CLOCK));
    }

    @Override
    public void refresh() {
        portal.load(RequestType.HOME, this::show);
    }

    private void show(Response home) {
        photo.getChildren().setAll(Ui.photo(home.getFile(), 104));
        Ui.Details info = new Ui.Details().add("Name", home.get("fullName")).add("Email", home.get("email"))
                .add("Last login", Ui.time(home.get("lastLogin")));
        if (portal.isStudent()) {
            info.add("Status", Ui.words(home.get("state"))).add("Supervisor", home.get("supervisor"))
                    .add("May register", Boolean.parseBoolean(home.get("allowedToRegister")) ? "Yes" : "No")
                    .add("Registration time", Ui.time(home.get("selectionStart")));
        }
        details.getChildren().setAll(info.view());
    }
}
