package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import network.RequestType;
import network.Response;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

final class WeeklyPlanPage extends Page {
    private static final List<String> DAYS = List.of("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri");
    private final GridPane grid = new GridPane();

    WeeklyPlanPage(Portal portal) {
        super(portal);
    }

    @Override
    public String title() {
        return "Weekly plan";
    }

    @Override
    public Node view() {
        grid.getStyleClass().add("timetable");
        for (int i = 0; i < DAYS.size(); i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / DAYS.size());
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }
        return Ui.scrolling(Ui.body(grid));
    }

    @Override
    public void refresh() {
        portal.load(RequestType.WEEKLY_PLAN, this::show);
    }

    private void show(Response response) {
        grid.getChildren().clear();
        for (int i = 0; i < DAYS.size(); i++) {
            String day = DAYS.get(i);
            Label head = new Label(day);
            head.getStyleClass().add("timetable-head");
            head.setMaxWidth(Double.MAX_VALUE);
            VBox column = new VBox(5);
            column.setMinHeight(330);
            column.setStyle("-fx-padding: 5;");
            response.table("classes").stream().filter(row -> row.get("time").startsWith(day))
                    .sorted(Comparator.comparing(row -> row.get("time")))
                    .forEach(row -> column.getChildren().add(card(row)));
            grid.add(head, i, 0);
            grid.add(column, i, 1);
        }
    }

    private static Node card(Map<String, String> row) {
        Label name = new Label(row.get("course"));
        name.setWrapText(true);
        Label time = new Label(row.get("time").substring(4));
        time.getStyleClass().add("muted");
        Label master = new Label(row.get("master"));
        master.getStyleClass().add("muted");
        master.setWrapText(true);
        VBox card = new VBox(2, name, time, master);
        card.getStyleClass().add("timetable-class");
        return card;
    }
}
