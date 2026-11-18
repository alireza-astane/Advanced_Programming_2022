package ui.pages;

import network.RequestType;
import ui.Portal;
import ui.Ui;

final class CalendarPage extends TablePage {
    CalendarPage(Portal portal) {
        super(portal, "Calendar", RequestType.CW_CALENDAR, "entries", "Nothing is scheduled.");
        table.column("Due", row -> Ui.time(row.get("time"))).column("Course", "course").column("Item", "title");
    }
}
