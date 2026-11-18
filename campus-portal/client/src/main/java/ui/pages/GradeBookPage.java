package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextInputDialog;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Form;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.util.Map;

final class GradeBookPage extends Page {
    private final DataTable table = new DataTable("Choose a course to see its students.");
    private final ChoiceBox<Form.Option> course = new ChoiceBox<>();
    private final javafx.scene.control.Button publish = Ui.button("Publish temporary grades", this::publish);
    private final javafx.scene.control.Button finish = Ui.primary("Publish final grades", this::finish);

    GradeBookPage(Portal portal) {
        super(portal);
        table.column("Student", "student").column("Number", "number").column("Grade", "value")
                .column("State", row -> Ui.words(row.get("state"))).column("Objection", "protest")
                .column("Your answer", "answer")
                .actions(150, DataTable.Action.when("Grade", GradeBookPage::editable, this::grade),
                        DataTable.Action.when("Answer", row -> editable(row) && !row.get("protest").isEmpty(), this::answer));
        course.setOnAction(event -> refreshScores());
    }

    private static boolean editable(Map<String, String> row) {
        return !row.get("state").equals("FINAL");
    }

    @Override
    public String title() {
        return "Grade book";
    }

    @Override
    public Node view() {
        return Ui.bodyWith(table.view(), Ui.row(course, publish, finish));
    }

    @Override
    public void refresh() {
        portal.load(RequestType.TEACHING_COURSES, response -> {
            var options = Choices.fromRows(response.table("courses"), "id", "name");
            if (!course.getItems().equals(options)) {
                Form.Option current = course.getValue();
                course.getItems().setAll(options);
                course.setValue(options.contains(current) ? current : options.isEmpty() ? null : options.get(0));
            }
            refreshScores();
        });
    }

    private void refreshScores() {
        if (course.getValue() != null) {
            portal.load(RequestType.COURSE_SCORES, this::show, course.getValue().value());
        }
    }

    private void show(Response response) {
        table.setRows(response.table("scores"));
        publish.setDisable(!Boolean.parseBoolean(response.get("allEntered")) || !response.get("stage").equals("NOT_PUBLISHED"));
        finish.setDisable(!response.get("stage").equals("TEMPORARY"));
    }

    private void grade(Map<String, String> row) {
        TextInputDialog dialog = new TextInputDialog(row.get("value"));
        dialog.setTitle("Grade for " + row.get("student"));
        dialog.setHeaderText(null);
        dialog.setContentText("Grade from 0 to 20");
        dialog.initOwner(portal.stage());
        dialog.showAndWait().ifPresent(value -> portal.act(RequestType.SET_SCORE, "The grade was saved.", row.get("id"), value));
    }

    private void answer(Map<String, String> row) {
        Form form = new Form().area("answer", "Answer", row.get("answer")).text("value", "New grade", row.get("value"));
        Ui.formDialog(portal, "Answer " + row.get("student"), "Send", form,
                f -> new Request(RequestType.ANSWER_PROTEST, row.get("id"), f.get("answer"), f.get("value")),
                "Your answer was sent.", response -> refresh());
    }

    private void publish() {
        portal.act(RequestType.PUBLISH_SCORES, "Temporary grades were published.", course.getValue().value());
    }

    private void finish() {
        Ui.confirm("Publish final grades? They cannot be changed afterwards.",
                () -> portal.act(RequestType.FINALIZE_SCORES, "Final grades were published.", course.getValue().value()));
    }
}
