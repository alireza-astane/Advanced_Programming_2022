package logic;

import database.DataManager;
import database.models.*;
import logic.enums.StudentState;
import logic.enums.StudentType;
import logic.enums.UniRequestType;
import network.Call;
import network.Problem;
import network.RequestType;
import network.Response;
import network.Routes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class UnitSelectionService {
    private UnitSelectionService() {
    }

    public static void register(Routes routes) {
        routes.on(RequestType.UNIT_SELECTION, Routes.STUDENT, UnitSelectionService::page);
        routes.on(RequestType.STAR_COURSE, Routes.STUDENT, UnitSelectionService::star);
        routes.on(RequestType.TAKE_COURSE, Routes.STUDENT, UnitSelectionService::take);
        routes.on(RequestType.DROP_COURSE, Routes.STUDENT, UnitSelectionService::drop);
        routes.on(RequestType.CHANGE_GROUP, Routes.STUDENT, UnitSelectionService::changeGroup);
        routes.on(RequestType.ASK_DEPUTY_FOR_COURSE, Routes.STUDENT, UnitSelectionService::askDeputy);
        routes.on(RequestType.SET_SELECTION_WINDOW, Routes.DEPUTY, UnitSelectionService::setWindow);
    }

    static boolean isOpen(Student student) {
        LocalDateTime now = LocalDateTime.now();
        return student.isAllowedToRegister() && student.getState() == StudentState.STUDYING
                && student.getSelectionStart() != null && student.getSelectionEnd() != null
                && !now.isBefore(student.getSelectionStart()) && !now.isAfter(student.getSelectionEnd());
    }

    private static Student openStudent(Call call) {
        Student student = People.student(call.user());
        if (!isOpen(student)) {
            throw new Problem("Your unit selection window is not open.");
        }
        return student;
    }

    private static Response page(Call call) {
        Student student = openStudent(call);
        Response response = new Response().put("end", Format.time(student.getSelectionEnd()))
                .put("units", unitsOf(student.getSelectedCourses()))
                .put("maxUnits", Configuration.number("maxUnits"))
                .put("ownDepartment", student.getDepartment().getId());
        CatalogService.addDepartments(response);
        List<Course> all = CatalogService.currentCourses();
        for (Course course : all) {
            Response.Row row = response.row("courses");
            CatalogService.fillCourse(row, course);
            row.put("selected", student.getSelectedCourses().contains(course))
                    .put("starred", student.getStarredCourses().contains(course))
                    .put("suggested", isSuggested(student, course))
                    .put("groups", siblings(course, all));
        }
        return response;
    }

    private static String siblings(Course course, List<Course> all) {
        StringBuilder groups = new StringBuilder();
        for (Course other : all) {
            if (other != course && other.getCourseNumber().equals(course.getCourseNumber())) {
                groups.append(groups.isEmpty() ? "" : ",").append(other.getId()).append(':').append(other.getGroupNumber());
            }
        }
        return groups.toString();
    }

    private static boolean isSuggested(Student student, Course course) {
        List<Course> passed = People.passedCourses(student);
        return course.getLevel() == student.getStudentType() && !isFull(course)
                && !student.getCourses().contains(course) && !alreadyPassed(passed, course)
                && passedAll(passed, course);
    }

    private static boolean alreadyPassed(List<Course> passed, Course course) {
        return passed.stream().anyMatch(done -> done.getCourseNumber().equals(course.getCourseNumber()));
    }

    private static boolean passedAll(List<Course> passed, Course course) {
        return course.getPrerequisites().stream().allMatch(required -> alreadyPassed(passed, required));
    }

    private static boolean isFull(Course course) {
        return course.getStudents().size() + course.getSelectingStudents().size() >= course.getCapacity();
    }

    private static int unitsOf(List<Course> courses) {
        return courses.stream().mapToInt(Course::getUnits).sum();
    }

    private static Response star(Call call) {
        List<Course> starred = People.student(call.user()).getStarredCourses();
        Course course = DataManager.get(Course.class, call.id(0));
        if (!starred.remove(course)) {
            starred.add(course);
        }
        return null;
    }

    private static Response take(Call call) {
        Student student = openStudent(call);
        Course course = DataManager.get(Course.class, call.id(0));
        checkCanTake(student, course, null);
        if (isFull(course)) {
            throw new Problem(course.getName() + " is full.");
        }
        select(student, course);
        return null;
    }

    private static void checkCanTake(Student student, Course course, Course replaced) {
        if (course.getLevel() != student.getStudentType()) {
            throw new Problem("This course is not offered at your level.");
        }
        List<Course> others = new ArrayList<>(student.getSelectedCourses());
        others.addAll(student.getCourses());
        others.remove(replaced);
        if (others.stream().anyMatch(other -> other.getCourseNumber().equals(course.getCourseNumber()))) {
            throw new Problem("You already have this course.");
        }
        List<Course> passed = People.passedCourses(student);
        if (alreadyPassed(passed, course)) {
            throw new Problem("You have already passed this course.");
        }
        if (!passedAll(passed, course)) {
            throw new Problem("You have not passed the prerequisites of " + course.getName() + ".");
        }
        checkClashes(course, others);
        checkLimits(student, course, replaced);
    }

    private static void checkClashes(Course course, List<Course> others) {
        for (Course other : others) {
            if (other.getExamTime().equals(course.getExamTime())) {
                throw new Problem("The exam clashes with " + other.getName() + ".");
            }
            if (classesOverlap(course, other)) {
                throw new Problem("The class time clashes with " + other.getName() + ".");
            }
            if (course.isGeneral() && other.isGeneral()) {
                throw new Problem("You can take only one general course.");
            }
        }
    }

    private static boolean classesOverlap(Course first, Course second) {
        for (String a : first.getClassTimes()) {
            for (String b : second.getClassTimes()) {
                if (ClassTime.parse(a).overlaps(ClassTime.parse(b))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void checkLimits(Student student, Course course, Course replaced) {
        int units = unitsOf(student.getSelectedCourses()) + course.getUnits() - (replaced == null ? 0 : replaced.getUnits());
        if (units > Configuration.number("maxUnits")) {
            throw new Problem("You can take at most " + Configuration.number("maxUnits") + " units.");
        }
    }

    static void select(Student student, Course course) {
        if (!student.getSelectedCourses().contains(course)) {
            student.getSelectedCourses().add(course);
            course.getSelectingStudents().add(student);
        }
    }

    private static void unselect(Student student, Course course) {
        student.getSelectedCourses().remove(course);
        course.getSelectingStudents().remove(student);
    }

    static void replace(Student student, Course from, Course to) {
        unselect(student, from);
        select(student, to);
    }

    private static Response drop(Call call) {
        unselect(openStudent(call), DataManager.get(Course.class, call.id(0)));
        return null;
    }

    private static Response changeGroup(Call call) {
        Student student = openStudent(call);
        Course from = DataManager.get(Course.class, call.id(0));
        Course to = DataManager.get(Course.class, call.id(1));
        if (!student.getSelectedCourses().contains(from) || from == to
                || !from.getCourseNumber().equals(to.getCourseNumber())) {
            throw new Problem("Those are not two groups of a course you have taken.");
        }
        checkCanTake(student, to, from);
        if (!isFull(to)) {
            replace(student, from, to);
            return new Response().put("message", "Your group was changed.");
        }
        sendCourseRequest(call.user(), student, UniRequestType.GROUP_CHANGE, "groupRequest", to).setOtherCourseId(to.getId());
        return new Response().put("message", "That group is full, so your request was sent to the deputy.");
    }

    private static Response askDeputy(Call call) {
        Student student = openStudent(call);
        Course course = DataManager.get(Course.class, call.id(0));
        sendCourseRequest(call.user(), student, UniRequestType.COURSE, "courseRequest", course);
        return new Response().put("message", "Your request was sent to the deputy.");
    }

    private static UniRequest sendCourseRequest(User user, Student student, UniRequestType type, String label, Course course) {
        UniRequest request = EducationService.create(type, user, EducationService.deputyOf(student.getDepartment()),
                Configuration.label(label, user.getFullName(), student.getStudentNumber(), course.getName(), course.getGroupNumber()));
        request.setCourseId(type == UniRequestType.GROUP_CHANGE
                ? groupBeingLeft(student, course).getId() : course.getId());
        return request;
    }

    private static Course groupBeingLeft(Student student, Course target) {
        return student.getSelectedCourses().stream()
                .filter(course -> course.getCourseNumber().equals(target.getCourseNumber()))
                .findFirst().orElseThrow();
    }

    /** Arguments: level or ALL, entry year or 0, start and end of the window. */
    private static Response setWindow(Call call) {
        LocalDateTime start = call.time(2, "Start");
        LocalDateTime end = call.time(3, "End");
        if (!start.isBefore(end)) {
            throw new Problem("The window must end after it starts.");
        }
        int year = call.number(1, "Entry year");
        int changed = 0;
        for (Student student : DataManager.query(Student.class, "from Student where department = :p0",
                People.master(call.user()).getDepartment())) {
            if (matches(student, call.required(0, "Level"), year)) {
                student.setSelectionStart(start);
                student.setSelectionEnd(end);
                changed++;
            }
        }
        return new Response().put("message", "Window set for " + changed + " students.");
    }

    private static boolean matches(Student student, String level, int year) {
        boolean levelMatches = level.equals("ALL") || student.getStudentType() == StudentType.valueOf(level);
        return levelMatches && (year == 0 || student.getEntryYear() == year);
    }

    /** Confirms the choices of every student whose window has closed. */
    public static void finishExpiredSelections() {
        for (Student student : DataManager.query(Student.class,
                "select distinct s from Student s join s.selectedCourses where s.selectionEnd < :p0", LocalDateTime.now())) {
            finish(student);
        }
    }

    private static void finish(Student student) {
        List<Course> kept = new ArrayList<>(student.getSelectedCourses());
        List<Course> passed = People.passedCourses(student);
        boolean changed = true;
        while (changed) {
            changed = kept.removeIf(course -> !passedAll(passed, course) || !corequisitesMet(course, kept, passed));
        }
        for (Course course : List.copyOf(student.getSelectedCourses())) {
            unselect(student, course);
            confirm(student, course, kept.contains(course));
        }
    }

    private static boolean corequisitesMet(Course course, List<Course> kept, List<Course> passed) {
        return course.getCorequisites().stream().allMatch(required -> kept.contains(required)
                || alreadyPassed(passed, required));
    }

    private static void confirm(Student student, Course course, boolean accepted) {
        if (accepted) {
            enroll(student, course);
        }
        EducationService.notice(student.getUser(),
                Configuration.label(accepted ? "courseConfirmed" : "droppedAfterSelection", course.getName()));
    }

    static void enroll(Student student, Course course) {
        if (!student.getCourses().contains(course)) {
            student.getCourses().add(course);
            course.getStudents().add(student);
            ScoreService.createScore(student, course);
        }
    }
}
