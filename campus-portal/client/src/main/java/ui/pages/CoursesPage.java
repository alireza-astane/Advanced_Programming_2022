package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Form;
import ui.Portal;
import ui.Ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class CoursesPage extends TablePage {
    private final TextField search = Ui.search("Course name or number", this::applyFilter);
    private final TextField professor = Ui.search("Professor", this::applyFilter);
    private final ChoiceBox<String> department = Choices.filter("All departments", this::applyFilter);
    private final ChoiceBox<String> level = Choices.filter("All levels", this::applyFilter, "Undergraduate", "Masters", "Phd");
    private final ChoiceBox<String> units = Choices.filter("Any units", this::applyFilter, "1", "2", "3", "4");
    private List<Map<String, String>> courses = List.of();
    private List<Map<String, String>> masters = List.of();

    CoursesPage(Portal portal) {
        super(portal, "Courses", RequestType.COURSES, "courses", "No courses match.");
        table.column("Number", "number").column("Course", "name").column("Group", "group")
                .column("Department", "department").column("Professor", "master")
                .column("Level", row -> Ui.words(row.get("level"))).column("Units", "units")
                .column("Seats", row -> row.get("taken") + " / " + row.get("capacity"))
                .column("Classes", "classTimes").column("Exam", row -> Ui.time(row.get("examTime")));
        if (portal.isDeputy()) {
            table.actions(100, DataTable.Action.when("Edit", this::isOwn, this::edit),
                    new DataTable.Action("Delete", this::isOwn, this::delete, true));
        }
    }

    @Override
    protected Node[] above() {
        Node filters = Ui.row(search, professor, department, level, units);
        return portal.isDeputy()
                ? new Node[]{Ui.row(Ui.primary("New course", () -> edit(null))), filters}
                : new Node[]{filters};
    }

    @Override
    protected void loaded(Response response) {
        courses = response.table("courses");
        masters = response.table("masters");
        Choices.update(department, response.table("departments").stream().map(row -> row.get("name")).toList());
        applyFilter();
    }

    private void applyFilter() {
        table.setFilter(row -> (Ui.contains(row.get("name"), search.getText()) || Ui.contains(row.get("number"), search.getText()))
                && Ui.contains(row.get("master"), professor.getText())
                && Choices.matches(department, row.get("department"))
                && Choices.matches(level, Ui.words(row.get("level")))
                && Choices.matches(units, row.get("units")));
    }

    private String ownDepartment() {
        return masters.stream().filter(row -> row.get("name").equals(portal.fullName())).findFirst()
                .map(row -> row.get("departmentId")).orElse("");
    }

    private boolean isOwn(Map<String, String> row) {
        return row.get("departmentId").equals(ownDepartment());
    }

    private void delete(Map<String, String> row) {
        Ui.confirm("Delete " + row.get("name") + "? Its grades and course pages are removed too.",
                () -> portal.act(RequestType.DELETE_COURSE, "The course was deleted.", row.get("id")));
    }

    private String numbersFor(String ids) {
        List<String> wanted = Arrays.asList(ids.split(","));
        return courses.stream().filter(row -> wanted.contains(row.get("id"))).map(row -> row.get("number"))
                .distinct().collect(Collectors.joining(", "));
    }

    private String idsFor(String numbers) {
        List<String> ids = new ArrayList<>();
        for (String number : numbers.split(",")) {
            courses.stream().filter(row -> row.get("number").equalsIgnoreCase(number.trim())).findFirst()
                    .ifPresent(row -> ids.add(row.get("id")));
        }
        return String.join(",", ids);
    }

    private void edit(Map<String, String> row) {
        Map<String, String> c = row == null ? Map.of() : row;
        List<Map<String, String>> ownMasters = masters.stream()
                .filter(master -> master.get("departmentId").equals(ownDepartment())).toList();
        Form form = new Form().text("name", "Name", c.get("name")).text("number", "Number", c.get("number"))
                .text("group", "Group", c.getOrDefault("group", "1"))
                .choice("master", "Professor", Choices.fromRows(ownMasters, "id", "name"), c.get("masterId"))
                .choice("level", "Level", Choices.LEVELS, c.get("level"))
                .text("units", "Units", c.getOrDefault("units", "3"))
                .text("capacity", "Capacity", c.getOrDefault("capacity", "30"))
                .check("general", "General course", Boolean.parseBoolean(c.get("general")))
                .dateTime("exam", "Exam", c.get("examTime"))
                .text("classes", "Class times", c.get("classTimes"), "Sun 09:00-10:30; Tue 09:00-10:30")
                .text("prerequisites", "Prerequisites", numbersFor(c.getOrDefault("prerequisiteIds", "")), "CS101, CS201")
                .text("corequisites", "Corequisites", numbersFor(c.getOrDefault("corequisiteIds", "")), "CS330")
                .text("assistants", "TA student numbers", c.get("assistants"), "25102001");
        Ui.formDialog(portal, row == null ? "New course" : "Edit course", "Save", form, f -> new Request(RequestType.SAVE_COURSE,
                        c.getOrDefault("id", ""), f.get("name"), f.get("number"), f.get("group"), f.get("master"), f.get("level"),
                        f.get("units"), f.get("capacity"), f.get("general"), f.get("exam"), f.get("classes"),
                        idsFor(f.get("prerequisites")), idsFor(f.get("corequisites")), f.get("assistants")),
                "The course was saved.", response -> refresh());
    }
}
