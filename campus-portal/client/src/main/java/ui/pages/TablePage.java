package ui.pages;

import javafx.scene.Node;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Page;
import ui.Portal;
import ui.Ui;

/** A page that is mostly one table filled from one request. */
abstract class TablePage extends Page {
    protected final DataTable table;
    private final String title;
    private final RequestType type;
    private final String rows;
    private final String[] args;

    TablePage(Portal portal, String title, RequestType type, String rows, String emptyText, String... args) {
        super(portal);
        this.title = title;
        this.type = type;
        this.rows = rows;
        this.args = args;
        table = new DataTable(emptyText);
    }

    @Override
    public String title() {
        return title;
    }

    protected Node[] above() {
        return new Node[0];
    }

    @Override
    public Node view() {
        return Ui.bodyWith(table.view(), above());
    }

    @Override
    public void refresh() {
        portal.load(new Request(type, args), response -> {
            table.setRows(response.table(rows));
            loaded(response);
        });
    }

    protected void loaded(Response response) {
    }
}
