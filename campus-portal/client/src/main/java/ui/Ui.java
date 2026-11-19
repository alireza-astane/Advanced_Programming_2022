package ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import network.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Small building blocks shared by the pages. */
public final class Ui {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm", Locale.ENGLISH);

    private Ui() {
    }

    public static String time(String iso) {
        try {
            return iso == null || iso.isEmpty() ? "" : LocalDateTime.parse(iso).format(TIME);
        } catch (DateTimeParseException e) {
            return iso;
        }
    }

    /** Turns a value such as ASSOCIATE_PROFESSOR into Associate professor. */
    public static String words(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        String text = value.replace('_', ' ').toLowerCase(Locale.ENGLISH);
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    public static VBox body(Node... children) {
        VBox box = new VBox(children);
        box.getStyleClass().add("page-body");
        return box;
    }

    /** A page body where {@code growing} takes the remaining height. */
    public static VBox bodyWith(Node growing, Node... above) {
        VBox box = body(above);
        box.getChildren().add(growing);
        VBox.setVgrow(growing, Priority.ALWAYS);
        return box;
    }

    public static ScrollPane scrolling(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        return scroll;
    }

    public static Label section(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    public static Label muted(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted");
        label.setWrapText(true);
        return label;
    }

    public static HBox row(Node... children) {
        HBox box = new HBox(8, children);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    public static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }

    public static Button primary(String text, Runnable action) {
        Button button = button(text, action);
        button.getStyleClass().add("primary");
        return button;
    }

    public static TextField search(String prompt, Runnable changed) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.textProperty().addListener((observable, before, now) -> changed.run());
        return field;
    }

    public static boolean contains(String text, String search) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(search.trim().toLowerCase(Locale.ROOT));
    }

    /** Label and value pairs in two columns. */
    public static final class Details {
        private final GridPane grid = new GridPane();
        private int row;

        public Details() {
            grid.getStyleClass().add("details");
        }

        public Details add(String key, String value) {
            Label name = new Label(key);
            name.getStyleClass().add("detail-key");
            Label content = new Label(value == null || value.isEmpty() ? "-" : value);
            content.setWrapText(true);
            grid.add(name, 0, row);
            grid.add(content, 1, row++);
            return this;
        }

        public GridPane view() {
            return grid;
        }
    }

    public static Node photo(byte[] bytes, double size) {
        StackPane frame = new StackPane();
        frame.getStyleClass().add("photo-frame");
        frame.setMinSize(size, size);
        frame.setMaxSize(size, size);
        if (bytes != null) {
            ImageView image = new ImageView(new Image(new ByteArrayInputStream(bytes), size, size, true, true));
            frame.getChildren().add(image);
        } else if (size >= 60) {
            frame.getChildren().add(muted("No photo"));
        }
        return frame;
    }

    public static void confirm(String question, Runnable action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, question, ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(null);
        alert.setTitle("Please confirm");
        alert.showAndWait().filter(ButtonType.OK::equals).ifPresent(answer -> action.run());
    }

    /** Builds the request a form describes, or reports why it cannot. */
    public interface RequestBuilder {
        Request build(Form form) throws IOException;
    }

    /** Shows a form. It stays open with the error if the server refuses the request. */
    public static void formDialog(Portal portal, String title, String submit, Form form, RequestBuilder builder,
                                  String success, Consumer<network.Response> after) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(portal.stage());
        dialog.getDialogPane().getStylesheets().addAll(portal.stage().getScene().getStylesheets());
        dialog.getDialogPane().getStyleClass().addAll(portal.stage().getScene().getRoot().getStyleClass());
        ButtonType confirm = new ButtonType(submit, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirm, ButtonType.CANCEL);
        Label error = new Label();
        error.getStyleClass().add("error");
        error.setWrapText(true);
        dialog.getDialogPane().setContent(new VBox(10, form.view(), error));
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirm);
        confirmButton.getStyleClass().add("primary");
        confirmButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            event.consume();
            submit(portal, form, builder, error, response -> {
                dialog.close();
                if (!success.isEmpty()) {
                    portal.showMessage(success);
                }
                after.accept(response);
            });
        });
        dialog.show();
    }

    private static void submit(Portal portal, Form form, RequestBuilder builder, Label error,
                               Consumer<network.Response> done) {
        try {
            portal.request(builder.build(form), done, error::setText);
        } catch (IOException e) {
            error.setText("That file could not be read.");
        }
    }

    public static Supplier<javafx.stage.Window> ownerOf(Portal portal) {
        return portal::stage;
    }
}
