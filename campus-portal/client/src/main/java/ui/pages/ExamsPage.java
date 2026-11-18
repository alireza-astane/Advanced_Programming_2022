package ui.pages;

import network.RequestType;
import ui.Portal;
import ui.Ui;

final class ExamsPage extends TablePage {
    ExamsPage(Portal portal) {
        super(portal, "Exams", RequestType.EXAMS, "exams", "You have no exams.");
        table.column("Date and time", row -> Ui.time(row.get("time"))).column("Course", "course").column("Number", "number");
    }
}
