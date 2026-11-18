package ui.pages;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import network.Request;
import network.RequestType;
import network.Response;
import ui.Form;
import ui.Portal;
import ui.Ui;

import java.util.List;
import java.util.Map;

final class RequestsPage extends TablePage {
    private List<Map<String, String>> departments = List.of();

    RequestsPage(Portal portal) {
        super(portal, "Requests", RequestType.MY_REQUESTS, "requests", "You have not made any requests.");
        table.column("Date", row -> Ui.time(row.get("time"))).column("Request", row -> Ui.words(row.get("type")))
                .column("Status", row -> Ui.words(row.get("result"))).column("Details", "text");
    }

    @Override
    protected Node[] above() {
        FlowPane actions = new FlowPane(8, 8);
        String level = portal.studentType();
        if (!level.equals("PHD")) {
            actions.getChildren().add(Ui.button("Recommendation letter", this::recommendation));
        }
        actions.getChildren().add(Ui.button("Enrollment certificate", () ->
                portal.act(RequestType.REQUEST_ENROLLMENT, "Your certificate is ready below.")));
        if (level.equals("UNDERGRADUATE")) {
            actions.getChildren().add(Ui.button("Minor", this::minor));
        }
        if (level.equals("MASTERS")) {
            actions.getChildren().add(Ui.button("Dormitory", () ->
                    portal.act(RequestType.REQUEST_DORMITORY, "Your request was decided.")));
        }
        if (level.equals("PHD")) {
            actions.getChildren().add(Ui.button("Dissertation defense", () ->
                    portal.act(RequestType.REQUEST_DEFENSE, "A defense date was assigned.")));
        }
        actions.getChildren().add(Ui.button("Withdraw from university", () ->
                Ui.confirm("Ask to withdraw from the university? If accepted, you can no longer log in.",
                        () -> portal.act(RequestType.REQUEST_WITHDRAWAL, "Your request was sent to the deputy."))));
        return new Node[]{actions};
    }

    @Override
    protected void loaded(Response response) {
        departments = response.table("departments");
    }

    private void minor() {
        Form form = new Form().choice("department", "Department", Choices.fromRows(departments, "id", "name"), null);
        Ui.formDialog(portal, "Minor request", "Send", form,
                f -> new Request(RequestType.REQUEST_MINOR, f.get("department")),
                "Your request was sent to both deputies.", response -> refresh());
    }

    private void recommendation() {
        portal.load(RequestType.MASTERS, response -> {
            Form form = new Form().choice("master", "Professor", Choices.fromRows(response.table("masters"), "id", "name"), null);
            Ui.formDialog(portal, "Recommendation letter", "Send", form,
                    f -> new Request(RequestType.REQUEST_RECOMMENDATION, f.get("master")),
                    "Your request was sent.", sent -> refresh());
        });
    }
}
