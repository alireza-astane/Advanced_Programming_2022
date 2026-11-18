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

import java.util.List;
import java.util.Map;

final class ProfessorsPage extends TablePage {
    private final TextField name = Ui.search("Name", this::applyFilter);
    private final ChoiceBox<String> grade = Choices.filter(this::applyFilter,
            "Assistant professor", "Associate professor", "Professor");
    private final ChoiceBox<String> department = Choices.filter(this::applyFilter);
    private List<Map<String, String>> masters = List.of();

    ProfessorsPage(Portal portal) {
        super(portal, "Professors", RequestType.MASTERS, "masters", "No professors match.");
        table.column("Name", "name").column("Department", "department")
                .column("Grade", row -> Ui.words(row.get("grade")))
                .column("Position", row -> Ui.words(row.get("position"))).column("Email", "email")
                .column("Room", "room").column("Phone", "phone");
        if (portal.isHead()) {
            table.actions(260, DataTable.Action.when("Edit", this::isManaged, this::edit),
                    DataTable.Action.when("Make deputy", row -> isManaged(row) && row.get("position").equals("NORMAL"),
                            row -> portal.act(RequestType.SET_DEPUTY, "The deputy was appointed.", row.get("id"))),
                    DataTable.Action.when("End deputy role", row -> isManaged(row) && row.get("position").equals("DEPUTY"),
                            row -> portal.act(RequestType.REMOVE_DEPUTY, "The deputy role was ended.", row.get("id"))),
                    new DataTable.Action("Remove", row -> isManaged(row) && row.get("position").equals("NORMAL"),
                            this::remove, true));
        }
    }

    @Override
    protected Node[] above() {
        Node filters = Ui.row(name, grade, department);
        return portal.isHead() || portal.isDeputy()
                ? new Node[]{Ui.row(Ui.primary("New professor", () -> edit(null))), filters}
                : new Node[]{filters};
    }

    @Override
    protected void loaded(Response response) {
        masters = response.table("masters");
        Choices.update(department, response.table("departments").stream().map(row -> row.get("name")).toList());
        applyFilter();
    }

    private void applyFilter() {
        table.setFilter(row -> Ui.contains(row.get("name"), name.getText())
                && Choices.matches(grade, Ui.words(row.get("grade")))
                && Choices.matches(department, row.get("department")));
    }

    private boolean isManaged(Map<String, String> row) {
        String own = masters.stream().filter(master -> master.get("name").equals(portal.fullName())).findFirst()
                .map(master -> master.get("departmentId")).orElse("");
        return row.get("departmentId").equals(own) && !row.get("position").equals("HEAD");
    }

    private void remove(Map<String, String> row) {
        Ui.confirm("Remove " + row.get("name") + " from the department?",
                () -> portal.act(RequestType.DELETE_MASTER, "The professor was removed.", row.get("id")));
    }

    private void edit(Map<String, String> row) {
        Map<String, String> m = row == null ? Map.of() : row;
        Form form = new Form().text("name", "Full name", m.get("name"));
        if (row == null) {
            form.text("username", "Username", "").password("password", "Password");
        }
        form.text("email", "Email", m.get("email")).text("phone", "Phone", m.get("phone"))
                .text("national", "National number", m.get("nationalNumber"))
                .text("number", "Professor number", m.get("number")).text("room", "Room", m.get("room"))
                .choice("grade", "Grade", Choices.GRADES, m.get("grade"));
        Ui.formDialog(portal, row == null ? "New professor" : "Edit professor", "Save", form, f ->
                        new Request(RequestType.SAVE_MASTER, m.getOrDefault("id", ""), f.get("name"),
                                row == null ? f.get("username") : "", row == null ? f.get("password") : "",
                                f.get("email"), f.get("phone"), f.get("national"), f.get("number"), f.get("room"), f.get("grade")),
                "The professor was saved.", response -> refresh());
    }
}
