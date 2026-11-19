package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Page;
import ui.Portal;
import ui.Ui;

final class DepartmentGradesPage extends Page {
    private final DataTable scores = new DataTable("No published grades match.");
    private final DataTable summaries = new DataTable("No courses.");
    private final TextField student = Ui.search("Student name or number", this::applyFilter);
    private final TextField master = Ui.search("Professor", this::applyFilter);
    private final ChoiceBox<String> course = Choices.filter("All courses", this::applyFilter);
    private final ChoiceBox<String> state = Choices.filter("All states", this::applyFilter, "Temporary", "Final");

    DepartmentGradesPage(Portal portal) {
        super(portal);
        scores.column("Course", "course").column("Professor", "master").column("Student", "student")
                .column("Number", "number").column("Grade", "value").column("State", row -> Ui.words(row.get("state")))
                .column("Objection", "protest").column("Answer", "answer");
        summaries.column("Course", "course").column("Average", "average").column("Passed", "passed")
                .column("Failed", "failed").column("Average of those who passed", "averageOfPassed");
    }

    @Override
    public String title() {
        return "Department grades";
    }

    @Override
    public Node view() {
        VBox.setVgrow(scores.view(), Priority.ALWAYS);
        summaries.view().setPrefHeight(190);
        VBox box = Ui.body(Ui.row(student, master, course, state), scores.view(), Ui.section("Course summaries"), summaries.view());
        return box;
    }

    @Override
    public void refresh() {
        portal.load(new Request(RequestType.DEPARTMENT_SCORES), this::show);
    }

    private void show(Response response) {
        scores.setRows(response.table("scores"));
        summaries.setRows(response.table("summaries"));
        Choices.update(course, response.table("summaries").stream().map(row -> row.get("course")).distinct().toList());
        applyFilter();
    }

    private void applyFilter() {
        scores.setFilter(row -> (Ui.contains(row.get("student"), student.getText()) || Ui.contains(row.get("number"), student.getText()))
                && Ui.contains(row.get("master"), master.getText()) && Choices.matches(course, row.get("course"))
                && Choices.matches(state, Ui.words(row.get("state"))));
    }
}
