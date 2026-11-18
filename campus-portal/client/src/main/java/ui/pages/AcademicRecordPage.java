package ui.pages;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import network.RequestType;
import network.Response;
import ui.Portal;
import ui.Ui;

/** A student's own record, or, when given a student id, the record a deputy opened. */
final class AcademicRecordPage extends TablePage {
    private final StackPane summary = new StackPane();

    AcademicRecordPage(Portal portal, String studentId) {
        super(portal, "Academic record", studentId == null ? RequestType.TRANSCRIPT : RequestType.STUDENT_TRANSCRIPT,
                "courses", "No courses yet.", studentId == null ? new String[0] : new String[]{studentId});
        summary.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        table.column("Course", "course").column("Term", "term").column("Units", "units").column("Grade", "value");
    }

    @Override
    protected Node[] above() {
        return new Node[]{summary};
    }

    @Override
    protected void loaded(Response response) {
        summary.getChildren().setAll(new Ui.Details().add("Student", response.get("student"))
                .add("Student number", response.get("number")).add("Passed units", response.get("passedUnits"))
                .add("Average", response.get("average")).view());
    }
}
