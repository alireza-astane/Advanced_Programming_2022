package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import network.RequestType;
import network.Response;
import ui.Page;
import ui.DataTable;
import ui.Portal;
import ui.Ui;

import java.util.Map;

final class StudentSearchPage extends Page {
    private final DataTable table = new DataTable("No student numbers start with those digits.");
    private final TextField number = Ui.search("Type a student number", this::applyFilter);
    private final VBox details = new VBox(8);

    StudentSearchPage(Portal portal) {
        super(portal);
        table.column("Name", "name").column("Number", "number").column("Level", row -> Ui.words(row.get("level")))
                .column("Entry year", "entryYear");
        table.onSelect(this::open);
        details.getStyleClass().add("panel");
        details.setPrefWidth(340);
        details.getChildren().add(Ui.muted("Select a student to see their details."));
    }

    @Override
    public String title() {
        return "Student search";
    }

    @Override
    public Node view() {
        HBox split = new HBox(12, table.view(), details);
        HBox.setHgrow(table.view(), Priority.ALWAYS);
        return Ui.bodyWith(split, Ui.row(number, Ui.button("Message students", () -> MessengerPage.compose(portal, this::refresh))));
    }

    @Override
    public void refresh() {
        portal.load(RequestType.ALL_STUDENTS, response -> {
            table.setRows(response.table("students"));
            applyFilter();
        });
    }

    private void applyFilter() {
        table.setFilter(row -> row.get("number").startsWith(number.getText().trim()));
    }

    private void open(Map<String, String> row) {
        portal.load(RequestType.STUDENT_DETAILS, this::show, row.get("id"));
    }

    private void show(Response profile) {
        details.getChildren().setAll(Ui.photo(profile.getFile(), 96), new Ui.Details()
                .add("Name", profile.get("fullName")).add("National number", profile.get("nationalNumber"))
                .add("Student number", profile.get("number")).add("Phone", profile.get("phone"))
                .add("Email", profile.get("email")).add("Average", profile.get("average"))
                .add("Department", profile.get("department")).add("Supervisor", profile.get("supervisor"))
                .add("Entry year", profile.get("entryYear")).add("Level", Ui.words(profile.get("level")))
                .add("Status", Ui.words(profile.get("state"))).view());
    }
}
