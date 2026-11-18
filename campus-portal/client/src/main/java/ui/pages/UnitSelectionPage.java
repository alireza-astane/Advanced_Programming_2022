package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Form;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class UnitSelectionPage extends Page {
    private final DataTable department = courseTable("No courses in this department.");
    private final DataTable suggested = courseTable("No suggestions at the moment.");
    private final DataTable starred = courseTable("You have not starred any courses.");
    private final DataTable selected = courseTable("You have not taken any courses yet.");
    private final ChoiceBox<Form.Option> departmentChoice = new ChoiceBox<>();
    private final ChoiceBox<String> sort = new ChoiceBox<>();
    private final Label status = new Label();
    private List<Map<String, String>> courses = List.of();

    UnitSelectionPage(Portal portal) {
        super(portal);
        sort.getItems().addAll("Name", "Exam time", "Level", "Units");
        sort.setValue("Name");
        sort.setOnAction(event -> showDepartment());
        departmentChoice.setOnAction(event -> showDepartment());
    }

    private DataTable courseTable(String empty) {
        return new DataTable(empty).column("Number", "number").column("Course", "name").column("Group", "group")
                .column("Professor", "master").column("Units", "units")
                .column("Seats", row -> row.get("taken") + " / " + row.get("capacity")).column("Classes", "classTimes")
                .column("Exam", row -> Ui.time(row.get("examTime")))
                .actions(255, DataTable.Action.of("Star", this::star),
                        DataTable.Action.when("Take", row -> !chosen(row), this::take),
                        DataTable.Action.when("Ask deputy", row -> !chosen(row), this::ask),
                        new DataTable.Action("Drop", UnitSelectionPage::chosen, this::drop, true),
                        DataTable.Action.when("Change group", UnitSelectionPage::chosen, this::changeGroup));
    }

    private static boolean chosen(Map<String, String> row) {
        return Boolean.parseBoolean(row.get("selected"));
    }

    @Override
    public String title() {
        return "Unit selection";
    }

    @Override
    public Node view() {
        Node departmentTab = Ui.bodyWith(department.view(), Ui.row(departmentChoice, new Label("Sort by"), sort));
        TabPane tabs = new TabPane(tab("Department courses", departmentTab), tab("Suggested", Ui.bodyWith(suggested.view())),
                tab("Starred", Ui.bodyWith(starred.view())), tab("My selection", Ui.bodyWith(selected.view())));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return Ui.bodyWith(tabs, status);
    }

    private static Tab tab(String name, Node content) {
        return new Tab(name, content);
    }

    @Override
    public void refresh() {
        portal.load(RequestType.UNIT_SELECTION, this::show);
    }

    private void show(Response response) {
        courses = response.table("courses");
        status.setText(response.get("units") + " of " + response.get("maxUnits") + " units taken. Selection closes "
                + Ui.time(response.get("end")) + ".");
        var options = Choices.fromRows(response.table("departments"), "id", "name");
        if (departmentChoice.getItems().isEmpty()) {
            departmentChoice.getItems().setAll(options);
            departmentChoice.setValue(options.stream().filter(option -> option.value().equals(response.get("ownDepartment")))
                    .findFirst().orElse(null));
        }
        suggested.setRows(matching(row -> flag(row, "suggested") && !chosen(row)));
        starred.setRows(matching(row -> flag(row, "starred")));
        selected.setRows(matching(UnitSelectionPage::chosen));
        showDepartment();
    }

    private static boolean flag(Map<String, String> row, String key) {
        return Boolean.parseBoolean(row.get(key));
    }

    private List<Map<String, String>> matching(Predicate<Map<String, String>> filter) {
        return courses.stream().filter(filter).toList();
    }

    private void showDepartment() {
        if (departmentChoice.getValue() == null) {
            return;
        }
        List<Map<String, String>> rows = new ArrayList<>(matching(row ->
                row.get("departmentId").equals(departmentChoice.getValue().value())));
        rows.sort(Comparator.comparing(row -> switch (sort.getValue()) {
            case "Exam time" -> row.get("examTime");
            case "Level" -> row.get("level");
            case "Units" -> row.get("units");
            default -> row.get("name");
        }));
        department.setRows(rows);
    }

    private void star(Map<String, String> row) {
        portal.act(RequestType.STAR_COURSE, flag(row, "starred") ? "Star removed." : "Course starred.", row.get("id"));
    }

    private void take(Map<String, String> row) {
        portal.act(RequestType.TAKE_COURSE, row.get("name") + " was added to your selection.", row.get("id"));
    }

    private void ask(Map<String, String> row) {
        portal.act(RequestType.ASK_DEPUTY_FOR_COURSE, "", row.get("id"));
    }

    private void drop(Map<String, String> row) {
        portal.act(RequestType.DROP_COURSE, row.get("name") + " was removed.", row.get("id"));
    }

    private void changeGroup(Map<String, String> row) {
        if (row.get("groups").isEmpty()) {
            portal.showError("This course has only one group.");
            return;
        }
        List<Form.Option> groups = new ArrayList<>();
        for (String group : row.get("groups").split(",")) {
            String[] parts = group.split(":");
            groups.add(new Form.Option(parts[0], "Group " + parts[1]));
        }
        ChoiceDialog<Form.Option> dialog = new ChoiceDialog<>(groups.get(0), groups);
        dialog.setTitle("Change group");
        dialog.setHeaderText(null);
        dialog.setContentText("Move to");
        dialog.initOwner(portal.stage());
        dialog.showAndWait().ifPresent(group -> portal.act(RequestType.CHANGE_GROUP, "", row.get("id"), group.value()));
    }
}
