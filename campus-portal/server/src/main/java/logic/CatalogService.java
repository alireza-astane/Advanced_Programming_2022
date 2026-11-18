package logic;

import database.DataManager;
import database.models.*;
import logic.enums.*;
import network.Call;
import network.Passwords;
import network.Problem;
import network.RequestType;
import network.Response;
import network.Routes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** The lists of courses and professors, and the changes department staff make to them. */
public final class CatalogService {
    private CatalogService() {
    }

    public static void register(Routes routes) {
        routes.on(RequestType.COURSES, Routes.EVERYONE, call -> courses());
        routes.on(RequestType.MASTERS, Routes.EVERYONE, call -> masters());
        routes.on(RequestType.SAVE_COURSE, Routes.DEPUTY, CatalogService::saveCourse);
        routes.on(RequestType.DELETE_COURSE, Routes.DEPUTY, CatalogService::deleteCourse);
        routes.on(RequestType.SAVE_MASTER, Routes.STAFF, CatalogService::saveMaster);
        routes.on(RequestType.DELETE_MASTER, Routes.STAFF, CatalogService::deleteMaster);
        routes.on(RequestType.SET_DEPUTY, Routes.STAFF, CatalogService::setDeputy);
        routes.on(RequestType.REMOVE_DEPUTY, Routes.STAFF, CatalogService::removeDeputy);
        routes.on(RequestType.ADD_STUDENT, Routes.DEPUTY, CatalogService::addStudent);
    }

    static void addDepartments(Response response) {
        for (Department department : DataManager.all(Department.class)) {
            response.row("departments").put("id", department.getId())
                    .put("name", department.getDepartmentType().title);
        }
    }

    static String names(List<Course> courses) {
        return courses.stream().map(Course::getName).collect(Collectors.joining(", "));
    }

    private static String ids(List<Course> courses) {
        return courses.stream().map(course -> String.valueOf(course.getId())).collect(Collectors.joining(","));
    }

    static void fillCourse(Response.Row row, Course course) {
        row.put("id", course.getId()).put("name", course.getName()).put("number", course.getCourseNumber())
                .put("group", course.getGroupNumber())
                .put("department", course.getDepartment().getDepartmentType().title)
                .put("departmentId", course.getDepartment().getId())
                .put("master", course.getMaster().getUser().getFullName()).put("masterId", course.getMaster().getId())
                .put("level", course.getLevel()).put("units", course.getUnits()).put("capacity", course.getCapacity())
                .put("taken", course.getStudents().size() + course.getSelectingStudents().size())
                .put("general", course.isGeneral()).put("examTime", Format.time(course.getExamTime()))
                .put("classTimes", String.join("; ", course.getClassTimes()))
                .put("prerequisites", names(course.getPrerequisites()))
                .put("prerequisiteIds", ids(course.getPrerequisites()))
                .put("corequisites", names(course.getCorequisites()))
                .put("corequisiteIds", ids(course.getCorequisites()))
                .put("assistants", course.getAssistants().stream().map(Student::getStudentNumber)
                        .collect(Collectors.joining(",")));
    }

    static List<Course> currentCourses() {
        return DataManager.query(Course.class, "from Course where term = :p0 order by courseNumber, groupNumber",
                Configuration.text("currentTerm"));
    }

    private static Response courses() {
        Response response = new Response();
        addDepartments(response);
        for (Course course : currentCourses()) {
            fillCourse(response.row("courses"), course);
        }
        addMasterRows(response);
        return response;
    }

    private static Response masters() {
        Response response = new Response();
        addDepartments(response);
        addMasterRows(response);
        return response;
    }

    private static void addMasterRows(Response response) {
        List<Master> masters = new ArrayList<>(DataManager.all(Master.class));
        masters.sort(Comparator.comparing(master -> master.getUser().getFullName()));
        for (Master master : masters) {
            User user = master.getUser();
            response.row("masters").put("id", master.getId()).put("name", user.getFullName())
                    .put("username", user.getUsername())
                    .put("department", master.getDepartment().getDepartmentType().title)
                    .put("departmentId", master.getDepartment().getId())
                    .put("grade", master.getGrade()).put("position", master.getMasterType())
                    .put("email", user.getEmail()).put("phone", user.getPhone())
                    .put("nationalNumber", user.getNationalNumber())
                    .put("room", master.getRoomNumber()).put("number", master.getMasterNumber());
        }
    }

    /** Arguments: id (empty when new), name, number, group, professor, level, units, capacity, general, exam, class times, prerequisites, corequisites, TA numbers. */
    private static Response saveCourse(Call call) {
        Department department = People.master(call.user()).getDepartment();
        boolean isNew = call.text(0).isEmpty();
        Course course = isNew ? new Course() : ownCourse(call.id(0), department);
        course.setName(call.required(1, "Course name"));
        course.setCourseNumber(call.required(2, "Course number"));
        course.setGroupNumber(call.number(3, "Group"));
        course.setMaster(DataManager.get(Master.class, call.number(4, "Professor")));
        course.setLevel(StudentType.valueOf(call.required(5, "Level")));
        course.setUnits(positive(call.number(6, "Units"), "Units"));
        course.setCapacity(positive(call.number(7, "Capacity"), "Capacity"));
        course.setGeneral(Boolean.parseBoolean(call.text(8)));
        course.setExamTime(call.time(9, "Exam time"));
        course.setClassTimes(classTimes(call.list(10, ";")));
        course.setPrerequisites(coursesById(call.list(11, ",")));
        course.setCorequisites(coursesById(call.list(12, ",")));
        course.setDepartment(department);
        course.setTerm(Configuration.text("currentTerm"));
        if (isNew) {
            DataManager.save(course);
        }
        setAssistants(course, call.list(13, ","));
        return new Response().put("id", course.getId());
    }

    private static int positive(int value, String field) {
        if (value <= 0) {
            throw new Problem(field + " must be greater than zero.");
        }
        return value;
    }

    private static List<String> classTimes(List<String> times) {
        for (String time : times) {
            ClassTime.parse(time);
        }
        return new ArrayList<>(times);
    }

    private static List<Course> coursesById(List<String> ids) {
        List<Course> courses = new ArrayList<>();
        for (String id : ids) {
            courses.add(DataManager.get(Course.class, Integer.parseInt(id)));
        }
        return courses;
    }

    private static void setAssistants(Course course, List<String> numbers) {
        for (Student student : List.copyOf(course.getAssistants())) {
            if (!numbers.contains(student.getStudentNumber())) {
                student.getAssistingCourses().remove(course);
            }
        }
        for (String number : numbers) {
            Student student = People.studentByNumber(number);
            if (!student.getAssistingCourses().contains(course)) {
                student.getAssistingCourses().add(course);
            }
        }
    }

    private static Course ownCourse(int id, Department department) {
        Course course = DataManager.get(Course.class, id);
        if (course.getDepartment() != department) {
            throw new Problem("That course belongs to another department.");
        }
        return course;
    }

    private static Response deleteCourse(Call call) {
        Course course = ownCourse(call.id(0), People.master(call.user()).getDepartment());
        for (Student student : DataManager.all(Student.class)) {
            student.getCourses().remove(course);
            student.getAssistingCourses().remove(course);
            student.getSelectedCourses().remove(course);
            student.getStarredCourses().remove(course);
        }
        for (Course other : DataManager.all(Course.class)) {
            other.getPrerequisites().remove(course);
            other.getCorequisites().remove(course);
        }
        for (Class<?> dependent : List.of(Score.class, CourseContent.class, Exercise.class)) {
            DataManager.query(dependent, "from " + dependent.getSimpleName() + " where course = :p0", course)
                    .forEach(DataManager::delete);
        }
        DataManager.delete(course);
        return null;
    }

    /** Arguments: id (empty when new), name, username, password, email, phone, national number, professor number, room, grade. */
    private static Response saveMaster(Call call) {
        User actor = call.user();
        Department department = People.master(actor).getDepartment();
        boolean isNew = call.text(0).isEmpty();
        if (!People.isHead(actor) && !(isNew && People.isDeputy(actor))) {
            throw new Problem("Only the head of the department can do that.");
        }
        Master master = isNew ? newMaster(call, department) : ownMaster(call.id(0), department);
        fillUser(master.getUser(), call, 1, 4);
        master.setMasterNumber(call.required(7, "Professor number"));
        master.setRoomNumber(call.text(8));
        master.setGrade(MasterGrade.valueOf(call.required(9, "Grade")));
        return new Response().put("id", master.getId());
    }

    private static Master newMaster(Call call, Department department) {
        User user = newUser(call.required(2, "Username"), call.required(3, "Password"), UserType.MASTER);
        fillUser(user, call, 1, 4);
        DataManager.save(user);
        Master master = new Master();
        master.setUser(user);
        master.setDepartment(department);
        master.setMasterNumber(call.required(7, "Professor number"));
        master.setGrade(MasterGrade.valueOf(call.required(9, "Grade")));
        user.setMaster(master);
        DataManager.save(master);
        return master;
    }

    private static User newUser(String username, String password, UserType type) {
        if (DataManager.first(User.class, "from User where username = :p0", username) != null) {
            throw new Problem("That username is already taken.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(Passwords.hash(password));
        user.setUserType(type);
        user.setLastLogin(LocalDateTime.now());
        return user;
    }

    private static void fillUser(User user, Call call, int nameIndex, int contactIndex) {
        user.setFullName(call.required(nameIndex, "Name"));
        user.setEmail(call.text(contactIndex));
        user.setPhone(call.text(contactIndex + 1));
        user.setNationalNumber(call.text(contactIndex + 2));
    }

    private static Master ownMaster(int id, Department department) {
        Master master = DataManager.get(Master.class, id);
        if (master.getDepartment() != department) {
            throw new Problem("That professor belongs to another department.");
        }
        return master;
    }

    private static Master managedMaster(Call call) {
        if (!People.isHead(call.user())) {
            throw new Problem("Only the head of the department can do that.");
        }
        return ownMaster(call.id(0), call.user().getMaster().getDepartment());
    }

    private static Response deleteMaster(Call call) {
        Master master = managedMaster(call);
        if (master.getMasterType() != MasterType.NORMAL) {
            throw new Problem("Remove this professor's position first.");
        }
        if (!DataManager.query(Course.class, "from Course where master = :p0", master).isEmpty()) {
            throw new Problem("This professor still teaches courses.");
        }
        DataManager.query(Student.class, "from Student where supervisor = :p0", master)
                .forEach(student -> student.setSupervisor(null));
        User user = master.getUser();
        user.setMaster(null);
        user.setCanLogin(false);
        DataManager.delete(master);
        return null;
    }

    private static Response setDeputy(Call call) {
        Master master = managedMaster(call);
        Department department = master.getDepartment();
        if (department.getDeputy() != null) {
            throw new Problem("Remove the current deputy first.");
        }
        if (master.getMasterType() != MasterType.NORMAL) {
            throw new Problem("The head of the department cannot also be the deputy.");
        }
        department.setDeputy(master);
        master.setMasterType(MasterType.DEPUTY);
        master.getUser().setUserType(UserType.ASSISTANT);
        return null;
    }

    private static Response removeDeputy(Call call) {
        Master master = managedMaster(call);
        if (master.getDepartment().getDeputy() != master) {
            throw new Problem("That professor is not the deputy.");
        }
        master.getDepartment().setDeputy(null);
        master.setMasterType(MasterType.NORMAL);
        master.getUser().setUserType(UserType.MASTER);
        return null;
    }

    /** Arguments: name, username, password, email, phone, national number, student number, level, entry year, supervisor. */
    private static Response addStudent(Call call) {
        Department department = People.master(call.user()).getDepartment();
        User user = newUser(call.required(1, "Username"), call.required(2, "Password"), UserType.STUDENT);
        fillUser(user, call, 0, 3);
        DataManager.save(user);
        Student student = new Student();
        student.setUser(user);
        user.setStudent(student);
        student.setDepartment(department);
        student.setStudentNumber(call.required(6, "Student number"));
        student.setStudentType(StudentType.valueOf(call.required(7, "Level")));
        student.setEntryYear(call.number(8, "Entry year"));
        if (!call.text(9).isEmpty()) {
            student.setSupervisor(DataManager.get(Master.class, call.id(9)));
        }
        DataManager.save(student);
        return new Response().put("id", student.getId());
    }
}
