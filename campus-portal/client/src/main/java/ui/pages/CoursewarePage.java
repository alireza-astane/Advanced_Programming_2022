package ui.pages;

import network.RequestType;
import ui.DataTable;
import ui.Portal;
import ui.Ui;

final class CoursewarePage extends TablePage {
    CoursewarePage(Portal portal) {
        super(portal, "My courses", RequestType.CW_COURSES, "courses", "You have no course pages yet.");
        table.column("Number", "number").column("Course", "name").column("Professor", "master")
                .column("Your role", row -> Ui.words(row.get("role")))
                .actions(70, DataTable.Action.of("Open", row -> open(new CoursePage(portal, row.get("id"), row.get("name")))));
    }
}
