package ui.pages;

import javafx.scene.Node;
import logic.enums.UserType;
import network.RequestType;
import ui.Portal;
import ui.Shell;

/** The sidebar: which pages each kind of user can reach. */
public final class Navigation {
    private final Portal portal;
    private final Shell shell;

    public Navigation(Portal portal, Shell shell) {
        this.portal = portal;
        this.shell = shell;
    }

    public void build() {
        shell.addItem("Home", () -> new HomePage(portal));
        if (portal.isStudent()) {
            student();
        } else if (portal.isProfessor()) {
            professor();
        } else if (portal.role() == UserType.REGISTRAR) {
            shell.addGroup("Students");
            shell.addItem("Student search", () -> new StudentSearchPage(portal));
        }
        messages();
        shell.addGroup("Account");
        shell.addItem("Profile", () -> new ProfilePage(portal));
    }

    private void student() {
        shell.addGroup("Registration");
        shell.addItem("Courses", () -> new CoursesPage(portal));
        shell.addItem("Professors", () -> new ProfessorsPage(portal));
        Node selection = shell.addItem("Unit selection", () -> new UnitSelectionPage(portal));
        selection.setVisible(false);
        selection.setManaged(false);
        portal.load(RequestType.HOME, response -> {
            boolean open = Boolean.parseBoolean(response.get("selectionOpen")) && portal.isOnline();
            selection.setVisible(open);
            selection.setManaged(open);
        });
        shell.addGroup("Education");
        shell.addItem("Weekly plan", () -> new WeeklyPlanPage(portal));
        shell.addItem("Exams", () -> new ExamsPage(portal));
        shell.addItem("Requests", () -> new RequestsPage(portal));
        shell.addGroup("Grades");
        shell.addItem("Temporary grades", () -> new TemporaryGradesPage(portal));
        shell.addItem("Academic record", () -> new AcademicRecordPage(portal, null));
        courseware();
    }

    private void professor() {
        shell.addGroup("Registration");
        shell.addItem("Courses", () -> new CoursesPage(portal));
        shell.addItem("Professors", () -> new ProfessorsPage(portal));
        shell.addGroup("Education");
        shell.addItem("Weekly plan", () -> new WeeklyPlanPage(portal));
        shell.addItem("Exams", () -> new ExamsPage(portal));
        shell.addGroup("Grades");
        shell.addItem("Grade book", () -> new GradeBookPage(portal));
        if (portal.isDeputy()) {
            shell.addGroup("Department");
            shell.addItem("Department grades", () -> new DepartmentGradesPage(portal));
            shell.addItem("Students", () -> new DepartmentStudentsPage(portal));
            shell.addItem("Selection windows", () -> new SelectionWindowsPage(portal));
        }
        courseware();
    }

    private void courseware() {
        shell.addGroup("Courseware");
        shell.addItem("My courses", () -> new CoursewarePage(portal));
        shell.addItem("Calendar", () -> new CalendarPage(portal));
    }

    private void messages() {
        shell.addGroup("Messages");
        shell.addItem("Messenger", () -> new MessengerPage(portal));
        shell.addItem("Inbox", () -> new InboxPage(portal));
    }
}
