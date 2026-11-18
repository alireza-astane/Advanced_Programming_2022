package ui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** A labelled column of inputs whose values are read by name. */
public final class Form {
    public record Option(String value, String label) {
        @Override
        public String toString() {
            return label;
        }
    }

    private final GridPane grid = new GridPane();
    private final Map<String, Supplier<String>> values = new HashMap<>();
    private File chosenFile;
    private int row;

    public Form() {
        grid.setHgap(12);
        grid.setVgap(8);
        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(120);
        ColumnConstraints inputs = new ColumnConstraints();
        inputs.setHgrow(Priority.ALWAYS);
        inputs.setMinWidth(280);
        grid.getColumnConstraints().addAll(labels, inputs);
    }

    public Node view() {
        return grid;
    }

    private void add(String label, Node input) {
        grid.add(new Label(label), 0, row);
        grid.add(input, 1, row++);
    }

    private Form addText(String key, String label, TextInputControl input, String value) {
        input.setText(value == null ? "" : value);
        values.put(key, input::getText);
        add(label, input);
        return this;
    }

    public Form text(String key, String label, String value) {
        return addText(key, label, new TextField(), value);
    }

    public Form text(String key, String label, String value, String hint) {
        TextField field = new TextField();
        field.setPromptText(hint);
        return addText(key, label, field, value);
    }

    public Form password(String key, String label) {
        return addText(key, label, new PasswordField(), "");
    }

    public Form area(String key, String label, String value) {
        TextArea area = new TextArea();
        area.setPrefRowCount(4);
        area.setWrapText(true);
        return addText(key, label, area, value);
    }

    public Form choice(String key, String label, List<Option> options, String value) {
        ChoiceBox<Option> box = new ChoiceBox<>();
        box.getItems().setAll(options);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(Option option) {
                return option == null ? "" : option.label();
            }

            @Override
            public Option fromString(String text) {
                return null;
            }
        });
        options.stream().filter(option -> option.value().equals(value)).findFirst()
                .ifPresentOrElse(box::setValue, () -> box.getSelectionModel().selectFirst());
        values.put(key, () -> box.getValue() == null ? "" : box.getValue().value());
        add(label, box);
        return this;
    }

    public Form check(String key, String label, boolean value) {
        CheckBox box = new CheckBox();
        box.setSelected(value);
        values.put(key, () -> String.valueOf(box.isSelected()));
        add(label, box);
        return this;
    }

    public Form dateTime(String key, String label, String value) {
        LocalDateTime initial = value == null || value.isEmpty() ? null : LocalDateTime.parse(value);
        DatePicker date = new DatePicker(initial == null ? LocalDate.now() : initial.toLocalDate());
        TextField time = new TextField(initial == null ? "09:00" : initial.toLocalTime().toString());
        time.setPrefColumnCount(5);
        values.put(key, () -> date.getValue() == null ? "" : date.getValue() + "T" + time.getText().trim());
        HBox.setHgrow(date, Priority.ALWAYS);
        date.setMaxWidth(Double.MAX_VALUE);
        add(label, new HBox(6, date, time));
        return this;
    }

    public Form file(String label, Supplier<Window> owner) {
        Label name = new Label("No file chosen");
        name.getStyleClass().add("muted");
        Button choose = new Button("Choose file");
        choose.setOnAction(event -> {
            File file = new FileChooser().showOpenDialog(owner.get());
            if (file != null) {
                chosenFile = file;
                name.setText(file.getName());
            }
        });
        add(label, new HBox(8, choose, name));
        return this;
    }

    public String get(String key) {
        return values.get(key).get().trim();
    }

    public void setFile(File file) {
        chosenFile = file;
    }

    public String fileName() {
        return chosenFile == null ? "" : chosenFile.getName();
    }

    public byte[] fileBytes() throws IOException {
        return chosenFile == null ? null : Files.readAllBytes(chosenFile.toPath());
    }
}
