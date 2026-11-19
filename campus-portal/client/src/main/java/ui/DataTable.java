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

import java.util.LinkedHashMap;
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

    private static final double CHARACTER_WIDTH = 7.1;
    private static final double CELL_PADDING = 18;
    private static final double MAX_COLUMN_WIDTH = 430;
    private static final double MIN_COLUMN_WIDTH = 48;
    private static final double SCROLLBAR_ROOM = 18;

    private final Map<TableColumn<Map<String, String>, String>, Function<Map<String, String>, String>> textColumns =
            new LinkedHashMap<>();
    private final ObservableList<Map<String, String>> rows = FXCollections.observableArrayList();
    private final FilteredList<Map<String, String>> filtered = new FilteredList<>(rows);
    private final TableView<Map<String, String>> table = new TableView<>(filtered);

    public DataTable(String emptyText) {
        table.setPlaceholder(new Label(emptyText));
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.widthProperty().addListener((observable, before, width) -> fitColumns());
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
        textColumns.put(column, value);
        return this;
    }

    public DataTable actions(double width, Action... actions) {
        TableColumn<Map<String, String>, String> column = new TableColumn<>("");
        column.setMinWidth(width);
        column.setPrefWidth(width);
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
        fitColumns();
        if (selected != null) {
            newRows.stream().filter(row -> sameRecord(row, selected)).findFirst()
                    .ifPresent(row -> table.getSelectionModel().select(row));
        }
    }

    /** Sizes columns to their contents, sharing spare width or shrinking them all so the actions stay in view. */
    private void fitColumns() {
        double used = table.getColumns().stream().filter(column -> !textColumns.containsKey(column))
                .mapToDouble(TableColumn::getPrefWidth).sum();
        Map<TableColumn<Map<String, String>, String>, Double> natural = new LinkedHashMap<>();
        textColumns.forEach((column, value) -> natural.put(column, naturalWidth(column, value)));
        double total = natural.values().stream().mapToDouble(Double::doubleValue).sum();
        double available = table.getWidth() - SCROLLBAR_ROOM - used;
        if (available <= 0) {
            return;
        }
        double spare = available - total;
        double shrink = spare < 0 ? available / total : 1;
        natural.forEach((column, width) -> column.setPrefWidth(spare < 0
                ? Math.max(MIN_COLUMN_WIDTH, width * shrink) : width + spare / natural.size()));
    }

    private double naturalWidth(TableColumn<Map<String, String>, String> column,
                                Function<Map<String, String>, String> value) {
        int longest = column.getText().length() + 2;
        for (Map<String, String> row : rows) {
            longest = Math.max(longest, value.apply(row).length());
        }
        return Math.min(MAX_COLUMN_WIDTH, longest * CHARACTER_WIDTH + CELL_PADDING);
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
