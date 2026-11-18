package ui.pages;

import javafx.scene.Node;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Form;
import ui.Portal;
import ui.Ui;

import java.util.Map;

final class ContentPage extends TablePage {
    private final String contentId;
    private String role = "STUDENT";
    private final javafx.scene.control.Button add = Ui.primary("Add an item", () -> edit(null));

    ContentPage(Portal portal, String contentId, String title) {
        super(portal, title, RequestType.CW_CONTENT, "items", "This page has no items yet.", contentId);
        this.contentId = contentId;
        add.setVisible(false);
        table.column("Text", "text").column("File", "fileName")
                .actions(190, DataTable.Action.when("Download", row -> !row.get("fileName").isEmpty(),
                                row -> portal.download(RequestType.CW_ITEM_FILE, row.get("id"))),
                        DataTable.Action.when("Edit", row -> !role.equals("STUDENT"), this::edit),
                        new DataTable.Action("Delete", row -> role.equals("MASTER"), this::delete, true));
    }

    @Override
    protected Node[] above() {
        return new Node[]{add};
    }

    @Override
    protected void loaded(Response response) {
        role = response.get("role");
        add.setVisible(role.equals("MASTER"));
    }

    private void delete(Map<String, String> row) {
        portal.act(RequestType.CW_DELETE_ITEM, "The item was deleted.", row.get("id"));
    }

    private void edit(Map<String, String> row) {
        Form form = new Form().area("text", "Text", row == null ? "" : row.get("text"))
                .file("Picture, video, audio or PDF", Ui.ownerOf(portal));
        Ui.formDialog(portal, row == null ? "New item" : "Edit item", "Save", form,
                f -> new Request(RequestType.CW_SAVE_ITEM, contentId, row == null ? "" : row.get("id"), f.get("text"), f.fileName())
                        .withFile(f.fileBytes()),
                "The item was saved.", response -> refresh());
    }
}
