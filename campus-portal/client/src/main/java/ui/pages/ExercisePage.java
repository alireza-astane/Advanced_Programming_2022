package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Form;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.util.Map;

final class ExercisePage extends Page {
    private final String exerciseId;
    private final String name;
    private final StackPane details = new StackPane();
    private final DataTable answers = new DataTable("No answers yet.");
    private final Button submit = Ui.primary("Submit an answer", this::submit);
    private final Button download;
    private final Button ownFile = Ui.button("Download my answer", this::downloadOwn);
    private Response current;

    ExercisePage(Portal portal, String exerciseId, String name) {
        super(portal);
        this.exerciseId = exerciseId;
        this.name = name;
        download = Ui.button("Download the exercise", () -> portal.download(RequestType.CW_EXERCISE_FILE, exerciseId));
        details.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        answers.column("Student", "student").column("Number", "number").column("Sent", row -> Ui.time(row.get("time")))
                .column("Answer", "text").column("File", "fileName").column("Grade", "grade")
                .actions(150, DataTable.Action.when("Download", row -> !row.get("fileName").isEmpty(),
                                row -> portal.download(RequestType.CW_ANSWER_FILE, row.get("id"))),
                        DataTable.Action.of("Grade", this::grade));
    }

    @Override
    public String title() {
        return name;
    }

    @Override
    public Node view() {
        VBox body = Ui.bodyWith(answers.view(), details, Ui.row(download, submit, ownFile));
        hide(submit, ownFile, download);
        answers.view().setVisible(false);
        return body;
    }

    private static void hide(Node... nodes) {
        for (Node node : nodes) {
            show(node, false);
        }
    }

    private static void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    @Override
    public void refresh() {
        portal.load(RequestType.CW_EXERCISE, this::show, exerciseId);
    }

    private void show(Response exercise) {
        current = exercise;
        boolean student = exercise.get("role").equals("STUDENT");
        Ui.Details info = new Ui.Details().add("Description", exercise.get("description"))
                .add("Opens", Ui.time(exercise.get("opensAt"))).add("Free submission until", Ui.time(exercise.get("freeUntil")))
                .add("Closes", Ui.time(exercise.get("closesAt"))).add("Answer type", Ui.words(exercise.get("answerType")));
        if (student) {
            boolean sent = Boolean.parseBoolean(exercise.get("submitted"));
            info.add("Status", sent ? "Submitted " + Ui.time(exercise.get("uploadedAt")) : "Not submitted")
                    .add("Your answer", exercise.get("answerText")).add("Grade", exercise.get("grade"));
        }
        details.getChildren().setAll(info.view());
        show(download, !exercise.get("fileName").isEmpty());
        show(submit, student);
        show(ownFile, student && !exercise.get("answerFile").isEmpty());
        answers.view().setVisible(!student);
        answers.setRows(exercise.table("answers"));
    }

    private void downloadOwn() {
        portal.download(RequestType.CW_ANSWER_FILE, current.get("answerId"));
    }

    private void submit() {
        Form form = new Form();
        if (current.get("answerType").equals("TEXT")) {
            form.area("text", "Answer", current.get("answerText"));
        } else {
            form.text("text", "Note", current.get("answerText")).file("File", Ui.ownerOf(portal));
        }
        Ui.formDialog(portal, "Submit an answer", "Submit", form,
                f -> new Request(RequestType.CW_SUBMIT_ANSWER, exerciseId, f.get("text"), f.fileName()).withFile(f.fileBytes()),
                "Your answer was submitted.", response -> refresh());
    }

    private void grade(Map<String, String> row) {
        TextInputDialog dialog = new TextInputDialog(row.get("grade"));
        dialog.setTitle("Grade");
        dialog.setHeaderText(null);
        dialog.setContentText("Grade");
        dialog.initOwner(portal.stage());
        dialog.showAndWait().ifPresent(value -> portal.act(RequestType.CW_GRADE_ANSWER, "The grade was saved.", row.get("id"), value));
    }
}
