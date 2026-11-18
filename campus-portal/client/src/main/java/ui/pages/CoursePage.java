package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Form;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.util.List;

final class CoursePage extends Page {
    private final String courseId;
    private final String name;
    private final DataTable contents = new DataTable("No learning content yet.");
    private final DataTable exercises = new DataTable("No exercises yet.");
    private final DataTable members = new DataTable("Nobody has joined yet.");
    private final Label exam = new Label();
    private final HBox staffActions = new HBox(8);
    private final VBox memberSection = new VBox(6);
    private boolean professor;

    CoursePage(Portal portal, String courseId, String name) {
        super(portal);
        this.courseId = courseId;
        this.name = name;
        contents.column("Title", "name").column("Added", row -> Ui.time(row.get("time")))
                .actions(150, DataTable.Action.of("Open", row -> open(new ContentPage(portal, row.get("id"), row.get("name")))),
                        new DataTable.Action("Delete", row -> professor, row -> deleteContent(row.get("id")), true));
        exercises.column("Exercise", "name").column("Opens", row -> Ui.time(row.get("opensAt")))
                .column("Free until", row -> Ui.time(row.get("freeUntil"))).column("Closes", row -> Ui.time(row.get("closesAt")))
                .actions(70, DataTable.Action.of("Open", row -> open(new ExercisePage(portal, row.get("id"), row.get("name")))));
        members.column("Name", "name").column("Number", "number").column("Role", "role");
    }

    @Override
    public String title() {
        return name;
    }

    @Override
    public Node view() {
        contents.view().setPrefHeight(170);
        exercises.view().setPrefHeight(170);
        members.view().setPrefHeight(170);
        memberSection.getChildren().addAll(Ui.section("People"), members.view());
        memberSection.setVisible(false);
        memberSection.setManaged(false);
        VBox body = Ui.body(exam, staffActions, Ui.section("Learning content"), contents.view(),
                Ui.section("Exercises"), exercises.view(), memberSection);
        VBox.setVgrow(exercises.view(), Priority.SOMETIMES);
        return Ui.scrolling(body);
    }

    @Override
    public void refresh() {
        portal.load(RequestType.CW_COURSE, this::show, courseId);
    }

    private void show(Response course) {
        boolean wasProfessor = professor;
        professor = course.get("role").equals("MASTER");
        exam.setText("Final exam: " + Ui.time(course.get("examTime")));
        contents.setRows(course.table("contents"));
        exercises.setRows(course.table("exercises"));
        members.setRows(course.table("members"));
        memberSection.setVisible(professor);
        memberSection.setManaged(professor);
        if (professor && !wasProfessor) {
            staffActions.getChildren().setAll(Ui.primary("New content", this::addContent),
                    Ui.button("New exercise", this::addExercise), Ui.button("Add a person", this::addMember));
        }
    }

    private void deleteContent(String id) {
        Ui.confirm("Delete this content?", () -> portal.act(RequestType.CW_DELETE_CONTENT, "The content was deleted.", id));
    }

    private void addContent() {
        Ui.formDialog(portal, "New content", "Create", new Form().text("name", "Title", ""),
                f -> new Request(RequestType.CW_ADD_CONTENT, courseId, f.get("name")),
                "", response -> open(new ContentPage(portal, response.get("id"), "Content")));
    }

    private void addMember() {
        Form form = new Form().text("number", "Student number", "")
                .choice("role", "Add as", List.of(new Form.Option("false", "Student"), new Form.Option("true", "Teaching assistant")), "false");
        Ui.formDialog(portal, "Add a person", "Add", form,
                f -> new Request(RequestType.CW_ADD_MEMBER, courseId, f.get("number"), f.get("role")),
                "They were added and notified.", response -> refresh());
    }

    private void addExercise() {
        Form form = new Form().text("name", "Name", "").area("description", "Description", "")
                .dateTime("opens", "Opens", null).dateTime("free", "Free until", null).dateTime("closes", "Closes", null)
                .choice("type", "Answer type", List.of(new Form.Option("TEXT", "Written text"), new Form.Option("MEDIA", "Uploaded file")), "TEXT")
                .file("PDF", Ui.ownerOf(portal));
        Ui.formDialog(portal, "New exercise", "Create", form, f -> new Request(RequestType.CW_ADD_EXERCISE, courseId, f.get("name"),
                        f.get("description"), f.get("opens"), f.get("closes"), f.get("free"), f.get("type"), f.fileName())
                        .withFile(f.fileBytes()),
                "The exercise was created.", response -> refresh());
    }
}
