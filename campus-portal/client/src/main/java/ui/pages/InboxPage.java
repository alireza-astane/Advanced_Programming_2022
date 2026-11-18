package ui.pages;

import network.RequestType;
import ui.DataTable;
import ui.Portal;
import ui.Ui;

import java.util.Map;

final class InboxPage extends TablePage {
    InboxPage(Portal portal) {
        super(portal, "Inbox", RequestType.INBOX, "items", "Your inbox is empty.");
        table.column("Date", row -> Ui.time(row.get("time"))).column("From", "from")
                .column("Type", row -> Ui.words(row.get("type"))).column("Message", "text")
                .column("Status", row -> row.get("type").equals("NOTICE") ? "" : Ui.words(row.get("result")))
                .actions(120, DataTable.Action.when("Accept", InboxPage::open, row -> answer(row, true)),
                        new DataTable.Action("Reject", InboxPage::open, row -> answer(row, false), true));
    }

    private static boolean open(Map<String, String> row) {
        return Boolean.parseBoolean(row.get("canAnswer"));
    }

    private void answer(Map<String, String> row, boolean accepted) {
        portal.act(RequestType.ANSWER_REQUEST, accepted ? "Request accepted." : "Request rejected.",
                row.get("id"), String.valueOf(accepted));
    }
}
