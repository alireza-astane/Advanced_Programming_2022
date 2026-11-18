package database;

import database.models.*;
import logic.Configuration;
import logic.enums.*;
import network.Passwords;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Fills an empty database with made-up people and courses so the portal can be tried at once. */
public final class DemoData {
    public static final String PASSWORD = "demo1234";
    private static final Logger log = LogManager.getLogger(DemoData.class);
    private static final String TERM = "Fall 2026";
    private static final String PAST_TERM = "Fall 2025";

    private final String passwordHash = Passwords.hash(PASSWORD);
    private final LocalDate today = LocalDate.now();
    private int nextNumber = 1000;

    private DemoData() {
    }

    public static void loadIfEmpty() {
        if (!Boolean.parseBoolean(Configuration.setting("portal.demo", "PORTAL_DEMO_DATA", "true"))) {
            return;
        }
        DataManager.inTransaction(() -> {
            if (DataManager.all(User.class).isEmpty()) {
                new DemoData().create();
                log.info("Demo data created. Every demo account uses the password " + PASSWORD);
            }
            DataManager.all(User.class).forEach(user -> user.setLastLogin(LocalDateTime.now()));
        });
    }

    private void create() {
        user("admin", "Portal Administrator", UserType.ADMIN);
        user("registrar", "Mina Sohrabi", UserType.REGISTRAR);

        Department cs = department(DepartmentType.COMPUTER_SCIENCE);
        Master csHead = head(cs, "head.cs", "Bahram Vaziri", "M1001");
        Master csDeputy = deputy(cs, "deputy.cs", "Farzaneh Alavi", "M1002");
        Master karimi = master("prof.karimi", "Kaveh Karimi", UserType.MASTER, MasterType.NORMAL, cs, "M1003");
        Master moradi = master("prof.moradi", "Leila Moradi", UserType.MASTER, MasterType.NORMAL, cs, "M1004");
        Department math = department(DepartmentType.MATHEMATICS);
        Master mathHead = head(math, "head.math", "Dariush Nazari", "M2001");
        deputy(math, "deputy.math", "Roya Hashemi", "M2002");

        Student sara = student("student.sara", "Sara Tavakoli", StudentType.UNDERGRADUATE, cs, "23101001", 2023, karimi);
        Student omid = student("student.omid", "Omid Rostami", StudentType.UNDERGRADUATE, cs, "23101002", 2023, karimi);
        Student nika = student("student.nika", "Nika Parsa", StudentType.MASTERS, cs, "25102001", 2025, moradi);
        Student arman = student("student.arman", "Arman Jalali", StudentType.MASTERS, cs, "25102002", 2025, moradi);
        Student reza = student("student.reza", "Reza Kamali", StudentType.PHD, cs, "24103001", 2024, csHead);
        Student yasi = student("student.yasi", "Yasaman Ebadi", StudentType.PHD, math, "24203001", 2024, mathHead);
        Student lena = student("student.lena", "Lena Ahmadi", StudentType.UNDERGRADUATE, math, "24201001", 2024, mathHead);

        Course fundamentals = course("Programming Fundamentals", "CS101", 1, PAST_TERM, cs, karimi, StudentType.UNDERGRADUATE, 4, 60, -300, "Sun 09:00-10:30", "Tue 09:00-10:30");
        Course structures = course("Data Structures", "CS201", 1, TERM, cs, karimi, StudentType.UNDERGRADUATE, 3, 2, 40, "Sun 10:30-12:00", "Tue 10:30-12:00");
        Course structures2 = course("Data Structures", "CS201", 2, TERM, cs, moradi, StudentType.UNDERGRADUATE, 3, 30, 40, "Sat 13:30-15:00", "Mon 15:00-16:30");
        Course algorithms = course("Algorithms", "CS301", 1, TERM, cs, moradi, StudentType.UNDERGRADUATE, 3, 40, 42, "Mon 09:00-10:30", "Wed 09:00-10:30");
        Course databases = course("Databases", "CS320", 1, TERM, cs, moradi, StudentType.UNDERGRADUATE, 3, 35, 44, "Mon 13:30-15:00", "Wed 13:30-15:00");
        Course networks = course("Computer Networks", "CS330", 1, TERM, cs, csDeputy, StudentType.UNDERGRADUATE, 3, 30, 47, "Sun 10:30-12:00");
        Course systems = course("Distributed Systems", "CS510", 1, TERM, cs, csHead, StudentType.MASTERS, 3, 20, 45, "Thu 09:00-12:00");
        Course seminar = course("Research Seminar", "CS710", 1, TERM, cs, csHead, StudentType.PHD, 2, 10, 46, "Thu 14:00-16:00");
        Course calculus = course("Calculus", "MA101", 1, TERM, math, mathHead, StudentType.UNDERGRADUATE, 4, 80, 41, "Sat 09:00-10:30", "Mon 10:30-12:00");
        Course algebra = course("Linear Algebra", "MA201", 1, TERM, math, mathHead, StudentType.UNDERGRADUATE, 3, 60, 43, "Sat 10:30-12:00");
        structures.getPrerequisites().add(fundamentals);
        structures2.getPrerequisites().add(fundamentals);
        algorithms.getPrerequisites().add(structures);
        databases.getCorequisites().add(networks);
        otherDepartments();

        finished(sara, fundamentals, 18.25);
        finished(omid, fundamentals, 15.5);
        enroll(sara, calculus);
        enroll(sara, structures);
        enroll(omid, structures);
        enroll(omid, algebra);
        enroll(nika, systems);
        enroll(arman, systems);
        enroll(reza, seminar);
        enroll(yasi, algebra);
        enroll(lena, calculus);
        nika.getAssistingCourses().add(structures);
        sara.setSelectionStart(LocalDateTime.now().minusHours(1));
        sara.setSelectionEnd(LocalDateTime.now().plusDays(3));
        coursework(structures, omid);
        conversations(sara, omid, karimi);
    }

    private void otherDepartments() {
        String[][] people = {
                {"physics", "Nader Yazdani", "Shirin Fallah", "Quantum Mechanics", "Thermodynamics", "PH"},
                {"electrical", "Sina Bahrami", "Taraneh Golzar", "Circuit Theory", "Signals and Systems", "EE"},
                {"chemistry", "Ramin Shafiei", "Parisa Vahdat", "Organic Chemistry", "Physical Chemistry", "CH"},
                {"general", "Hooman Saberi", "Golnaz Asadi", "History of Science", "Philosophy of Science", "GS"}};
        DepartmentType[] types = {DepartmentType.PHYSICS, DepartmentType.ELECTRICAL_ENGINEERING,
                DepartmentType.CHEMISTRY, DepartmentType.GENERAL_STUDIES};
        for (int i = 0; i < types.length; i++) {
            String[] row = people[i];
            Department department = department(types[i]);
            Master head = head(department, "head." + row[0], row[1], "M" + (3001 + i * 10));
            deputy(department, "deputy." + row[0], row[2], "M" + (3002 + i * 10));
            Course first = course(row[3], row[5] + "101", 1, TERM, department, head, StudentType.UNDERGRADUATE, 3, 40, 48 + i, "Tue " + (13 + i) + ":00-" + (14 + i) + ":30");
            Course second = course(row[4], row[5] + "201", 1, TERM, department, head, StudentType.UNDERGRADUATE, 3, 40, 53 + i, "Wed " + (15 + i) + ":00-" + (16 + i) + ":30");
            boolean general = types[i] == DepartmentType.GENERAL_STUDIES;
            first.setGeneral(general);
            second.setGeneral(general);
        }
    }

    private void coursework(Course course, Student answering) {
        CourseContent content = new CourseContent();
        content.setCourse(course);
        content.setName("Week 1: Arrays and linked lists");
        content.setCreatedAt(LocalDateTime.now().minusDays(9));
        DataManager.save(content);
        ContentItem item = new ContentItem();
        item.setContent(content);
        item.setText("Read chapters one and two before Sunday's class.");
        content.getItems().add(item);
        DataManager.save(item);

        Exercise exercise = new Exercise();
        exercise.setCourse(course);
        exercise.setName("Homework 1: Linked lists");
        exercise.setDescription("Implement a doubly linked list and explain the cost of each operation.");
        exercise.setOpensAt(LocalDateTime.now().minusDays(5));
        exercise.setFreeUntil(LocalDateTime.now().plusDays(5));
        exercise.setClosesAt(LocalDateTime.now().plusDays(7));
        DataManager.save(exercise);
        Answer answer = new Answer();
        answer.setExercise(exercise);
        answer.setStudent(answering);
        answer.setText("Insertion and removal take constant time at either end.");
        exercise.getAnswers().add(answer);
        DataManager.save(answer);
    }

    private void conversations(Student first, Student second, Master supervisor) {
        Chat chat = new Chat();
        chat.getUsers().add(first.getUser());
        chat.getUsers().add(second.getUser());
        DataManager.save(chat);
        message(chat, second.getUser(), "Did you start the linked list homework?");
        message(chat, first.getUser(), "Yes, the removal case took me a while.");
        UniRequest request = new UniRequest();
        request.setRequestType(UniRequestType.RECOMMENDATION);
        request.setRequester(first.getUser());
        request.setResponsible(supervisor.getUser());
        request.setText("Sara Tavakoli (23101001) asks for a letter of recommendation.");
        DataManager.save(request);
    }

    private void message(Chat chat, User sender, String text) {
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setText(text);
        DataManager.save(message);
        chat.getMessages().add(message);
        chat.setLastMessage(text);
        chat.setLastMessageTime(message.getSentAt());
    }

    private User user(String username, String fullName, UserType type) {
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setUserType(type);
        user.setPassword(passwordHash);
        user.setLastLogin(LocalDateTime.now());
        user.setEmail(username + "@portal.example");
        user.setPhone("0900000" + nextNumber);
        user.setNationalNumber("000000" + nextNumber++);
        DataManager.save(user);
        return user;
    }

    private Department department(DepartmentType type) {
        Department department = new Department();
        department.setDepartmentType(type);
        DataManager.save(department);
        return department;
    }

    private Master head(Department department, String username, String name, String number) {
        Master master = master(username, name, UserType.MASTER, MasterType.HEAD, department, number);
        master.setGrade(MasterGrade.PROFESSOR);
        department.setHead(master);
        return master;
    }

    private Master deputy(Department department, String username, String name, String number) {
        Master master = master(username, name, UserType.ASSISTANT, MasterType.DEPUTY, department, number);
        master.setGrade(MasterGrade.ASSOCIATE_PROFESSOR);
        department.setDeputy(master);
        return master;
    }

    private Master master(String username, String name, UserType userType, MasterType type,
                          Department department, String number) {
        User user = user(username, name, userType);
        Master master = new Master();
        master.setUser(user);
        user.setMaster(master);
        master.setMasterType(type);
        master.setDepartment(department);
        master.setMasterNumber(number);
        master.setRoomNumber(number.substring(1));
        DataManager.save(master);
        return master;
    }

    private Student student(String username, String name, StudentType type, Department department,
                            String number, int year, Master supervisor) {
        User user = user(username, name, UserType.STUDENT);
        Student student = new Student();
        student.setUser(user);
        user.setStudent(student);
        student.setStudentType(type);
        student.setStudentNumber(number);
        student.setEntryYear(year);
        student.setDepartment(department);
        student.setSupervisor(supervisor);
        DataManager.save(student);
        return student;
    }

    private Course course(String name, String number, int group, String term, Department department, Master master,
                          StudentType level, int units, int capacity, int examInDays, String... classTimes) {
        Course course = new Course();
        course.setName(name);
        course.setCourseNumber(number);
        course.setGroupNumber(group);
        course.setTerm(term);
        course.setDepartment(department);
        course.setMaster(master);
        course.setLevel(level);
        course.setUnits(units);
        course.setCapacity(capacity);
        course.setExamTime(today.plusDays(examInDays).atTime(9, 0));
        course.setClassTimes(new ArrayList<>(List.of(classTimes)));
        DataManager.save(course);
        return course;
    }

    private Score enroll(Student student, Course course) {
        student.getCourses().add(course);
        course.getStudents().add(student);
        Score score = new Score();
        score.setStudent(student);
        score.setCourse(course);
        DataManager.save(score);
        return score;
    }

    private void finished(Student student, Course course, double value) {
        Score score = enroll(student, course);
        score.setEntered(true);
        score.setScoreValue(value);
        score.setScoreType(ScoreType.FINAL);
        student.getCourses().remove(course);
        course.getStudents().remove(student);
    }
}
