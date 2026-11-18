package ui.pages;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import network.Request;
import network.RequestType;
import ui.Form;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.util.ArrayList;
import java.util.List;

final class SelectionWindowsPage extends Page {
    private final Form form;

    SelectionWindowsPage(Portal portal) {
        super(portal);
        List<Form.Option> levels = new ArrayList<>(List.of(new Form.Option("ALL", "All levels")));
        levels.addAll(Choices.LEVELS);
        form = new Form().choice("level", "Level", levels, "ALL")
                .text("year", "Entry year", "0", "0 means every year")
                .dateTime("start", "Opens", null).dateTime("end", "Closes", null);
    }

    @Override
    public String title() {
        return "Selection windows";
    }

    @Override
    public Node view() {
        VBox panel = new VBox(12, Ui.muted("Choose who can select courses and when. It applies to students of your department."),
                form.view(), Ui.primary("Set window", this::save));
        panel.getStyleClass().add("panel");
        panel.setMaxWidth(560);
        return Ui.body(panel);
    }

    private void save() {
        portal.act(new Request(RequestType.SET_SELECTION_WINDOW, form.get("level"), form.get("year"), form.get("start"), form.get("end")),
                "", response -> { });
    }

    @Override
    public void refresh() {
    }
}
