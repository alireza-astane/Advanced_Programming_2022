package ui.pages;

import javafx.scene.control.ChoiceBox;
import ui.Form;
import ui.Ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Option lists used by filters and forms. */
final class Choices {
    static final String ANY = "Any";
    static final List<Form.Option> LEVELS = options("UNDERGRADUATE", "MASTERS", "PHD");
    static final List<Form.Option> GRADES = options("ASSISTANT_PROFESSOR", "ASSOCIATE_PROFESSOR", "PROFESSOR");

    private Choices() {
    }

    private static List<Form.Option> options(String... values) {
        List<Form.Option> options = new ArrayList<>();
        for (String value : values) {
            options.add(new Form.Option(value, Ui.words(value)));
        }
        return options;
    }

    static List<Form.Option> fromRows(List<Map<String, String>> rows, String valueKey, String labelKey) {
        return rows.stream().map(row -> new Form.Option(row.get(valueKey), row.get(labelKey))).toList();
    }

    static ChoiceBox<String> filter(Runnable changed, String... values) {
        ChoiceBox<String> box = new ChoiceBox<>();
        box.getItems().add(ANY);
        box.getItems().addAll(values);
        box.setValue(ANY);
        box.setOnAction(event -> changed.run());
        return box;
    }

    /** Replaces a filter's options without losing the current choice. */
    static void update(ChoiceBox<String> box, List<String> values) {
        List<String> items = new ArrayList<>();
        items.add(ANY);
        items.addAll(values);
        if (!box.getItems().equals(items)) {
            String current = box.getValue();
            box.getItems().setAll(items);
            box.setValue(items.contains(current) ? current : ANY);
        }
    }

    static boolean matches(ChoiceBox<String> box, String value) {
        return ANY.equals(box.getValue()) || box.getValue() == null || box.getValue().equals(value);
    }
}
