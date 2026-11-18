package ui.pages;

import network.Request;
import network.RequestType;
import ui.DataTable;
import ui.Form;
import ui.Portal;

import java.util.Map;

final class TemporaryGradesPage extends TablePage {
    TemporaryGradesPage(Portal portal) {
        super(portal, "Temporary grades", RequestType.MY_TEMPORARY_SCORES, "scores", "No temporary grades at the moment.");
        table.column("Course", "course").column("Grade", "value").column("Your objection", "protest")
                .column("Professor's answer", "answer").actions(80, DataTable.Action.of("Object", this::object));
    }

    private void object(Map<String, String> row) {
        Form form = new Form().area("text", "Objection", row.get("protest"));
        ui.Ui.formDialog(portal, "Objection to " + row.get("course"), "Send", form,
                f -> new Request(RequestType.PROTEST_SCORE, row.get("id"), f.get("text")),
                "Your objection was sent.", response -> refresh());
    }
}
