package ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/** A table of server rows that keeps its selection and scroll position when refreshed. */
public final class DataTable {
    public record Action(String label, Predicate<Map<String, String>> visible, Consumer<Map<String, String>> run,
                         boolean danger) {
        public static Action of(String label, Consumer<Map<String, String>> run) {
            return new Action(label, row -> true, run, false);
        }

        public static Action when(String label, Predicate<Map<String, String>> visible, Consumer<Map<String, String>> run) {
            return new Action(label, visible, run, false);
        }

        public static Action danger(String label, Consumer<Map<String, String>> run) {
            return new Action(label, row -> true, run, true);
        }
    }

    private final ObservableList<Map<String, String>> rows = FXCollections.observableArrayList();
    private final FilteredList<Map<String, String>> filtered = new FilteredList<>(rows);
    private final TableView<Map<String, String>> table = new TableView<>(filtered);

    public DataTable(String emptyText) {
        table.setPlaceholder(new Label(emptyText));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    public TableView<Map<String, String>> view() {
        return table;
    }

    public DataTable column(String title, String key) {
        return column(title, row -> row.getOrDefault(key, ""));
    }

    public DataTable column(String title, Function<Map<String, String>, String> value) {
        TableColumn<Map<String, String>, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(value.apply(cell.getValue())));
        table.getColumns().add(column);
        return this;
    }

    public DataTable actions(double width, Action... actions) {
        TableColumn<Map<String, String>, String> column = new TableColumn<>("");
        column.setMinWidth(width);
        column.setMaxWidth(width);
        column.setSortable(false);
        column.setCellFactory(ignored -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Map<String, String> row = empty || getTableRow() == null ? null : getTableRow().getItem();
                setGraphic(row == null ? null : buttons(row, actions));
            }
        });
        table.getColumns().add(column);
        return this;
    }

    private static HBox buttons(Map<String, String> row, Action[] actions) {
        HBox box = new HBox(4);
        for (Action action : actions) {
            if (action.visible().test(row)) {
                Button button = new Button(action.label());
                button.getStyleClass().setAll("link-button");
                if (action.danger()) {
                    button.getStyleClass().add("danger");
                }
                button.setOnAction(event -> action.run().accept(row));
                box.getChildren().add(button);
            }
        }
        return box;
    }

    public void setRows(List<Map<String, String>> newRows) {
        if (rows.equals(newRows)) {
            return;
        }
        Map<String, String> selected = table.getSelectionModel().getSelectedItem();
        rows.setAll(newRows);
        if (selected != null) {
            newRows.stream().filter(row -> sameRecord(row, selected)).findFirst()
                    .ifPresent(row -> table.getSelectionModel().select(row));
        }
    }

    private static boolean sameRecord(Map<String, String> first, Map<String, String> second) {
        return first.containsKey("id") && first.get("id").equals(second.get("id"));
    }

    public void setFilter(Predicate<Map<String, String>> filter) {
        filtered.setPredicate(filter);
    }

    public List<Map<String, String>> visibleRows() {
        return List.copyOf(filtered);
    }

    public Map<String, String> selected() {
        return table.getSelectionModel().getSelectedItem();
    }

    public void onSelect(Consumer<Map<String, String>> listener) {
        table.getSelectionModel().selectedItemProperty().addListener((observable, before, row) -> {
            if (row != null) {
                listener.accept(row);
            }
        });
    }
}
