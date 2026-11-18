package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Form;
import ui.Portal;
import ui.Ui;

import java.util.List;

final class DepartmentStudentsPage extends TablePage {
    private final TextField search = Ui.search("Name or student number", this::applyFilter);

    DepartmentStudentsPage(Portal portal) {
        super(portal, "Students", RequestType.DEPARTMENT_STUDENTS, "students", "No students match.");
        table.column("Name", "name").column("Number", "number").column("Entry year", "entryYear")
                .column("Level", row -> Ui.words(row.get("level")))
                .actions(110, DataTable.Action.of("Open record", row -> open(new AcademicRecordPage(portal, row.get("id")))));
    }

    @Override
    protected Node[] above() {
        return new Node[]{Ui.row(Ui.primary("New student", this::addStudent), search)};
    }

    @Override
    protected void loaded(Response response) {
        applyFilter();
    }

    private void applyFilter() {
        table.setFilter(row -> Ui.contains(row.get("name"), search.getText()) || Ui.contains(row.get("number"), search.getText()));
    }

    private void addStudent() {
        portal.load(RequestType.MASTERS, response -> {
            List<Form.Option> masters = Choices.fromRows(response.table("masters"), "id", "name");
            Form form = new Form().text("name", "Full name", "").text("username", "Username", "")
                    .password("password", "Password").text("email", "Email", "").text("phone", "Phone", "")
                    .text("national", "National number", "").text("number", "Student number", "")
                    .choice("level", "Level", Choices.LEVELS, null)
                    .text("year", "Entry year", String.valueOf(java.time.Year.now().getValue()))
                    .choice("supervisor", "Supervisor", masters, null);
            Ui.formDialog(portal, "New student", "Create", form, f -> new Request(RequestType.ADD_STUDENT, f.get("name"),
                            f.get("username"), f.get("password"), f.get("email"), f.get("phone"), f.get("national"),
                            f.get("number"), f.get("level"), f.get("year"), f.get("supervisor")),
                    "The student was added.", created -> refresh());
        });
    }
}
