package ui;

import javafx.scene.Node;

/** One screen inside the shell. */
public abstract class Page {
    protected final Portal portal;
    private Shell shell;

    protected Page(Portal portal) {
        this.portal = portal;
    }

    void attach(Shell shell) {
        this.shell = shell;
    }

    protected void open(Page page) {
        shell.open(page);
    }

    protected void back() {
        shell.back();
    }

    public abstract String title();

    public abstract Node view();

    /** Fetches fresh data. Called when the page opens and every few seconds afterwards. */
    public abstract void refresh();
}
