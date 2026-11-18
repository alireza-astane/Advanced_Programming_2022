package serverDataBase;

import logic.Configuration;
import logic.enums.*;
import network.Passwords;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Fills an empty database with made-up people and courses so the portal can be tried at once. */
public final class DemoData {
    public static final String PASSWORD = "demo1234";
    private static final Logger log = LogManager.getLogger(DemoData.class);

    private final String passwordHash = Passwords.hash(PASSWORD);
    private final LocalDate today = LocalDate.now();
    private int nextNational = 1000;

    private DemoData() {
    }

    public static void loadIfEmpty() {
        if (!Boolean.parseBoolean(Configuration.setting("portal.demo", "PORTAL_DEMO_DATA", "true"))) {
            return;
        }
        DataManager.inTransaction(() -> {
            if (DataManager.getUsers().isEmpty()) {
                new DemoData().create();
                log.info("Demo data created. Every demo account uses the password " + PASSWORD);
            }
            for (User user : DataManager.getUsers()) {
                user.setUserLastLoginTime(LocalDateTime.now());
            }
        });
    }

    private void create() {
        user("admin", "Portal Administrator", UserType.ADMIN);
        user("registrar", "Mina Sohrabi", UserType.MOHSENI);

        Department cs = department(DepartmentType.COMPUTER_SCIENCE);
        Department math = department(DepartmentType.MATHEMATICS);
        department(DepartmentType.PHYSICS);

        Master head = master("head.cs", "Bahram Vaziri", UserType.MASTER, MasterType.BOSS, MasterGrade.PROFESSOR, cs, "M1001", "301");
        Master deputy = master("deputy.cs", "Farzaneh Alavi", UserType.ASSISTANT, MasterType.ASSISTANT, MasterGrade.ASSOCIATE_PROFESSOR, cs, "M1002", "302");
        Master karimi = master("prof.karimi", "Kaveh Karimi", UserType.MASTER, MasterType.NORMAL, MasterGrade.ASSISTANT_PROFESSOR, cs, "M1003", "210");
        Master moradi = master("prof.moradi", "Leila Moradi", UserType.MASTER, MasterType.NORMAL, MasterGrade.ASSOCIATE_PROFESSOR, cs, "M1004", "212");
        cs.setDepartmentBossId(head.getId());
        cs.setDepartmentAssistantId(deputy.getId());
        Master mathHead = master("head.math", "Dariush Nazari", UserType.MASTER, MasterType.BOSS, MasterGrade.PROFESSOR, math, "M2001", "101");
        Master mathDeputy = master("deputy.math", "Roya Hashemi", UserType.ASSISTANT, MasterType.ASSISTANT, MasterGrade.ASSOCIATE_PROFESSOR, math, "M2002", "102");
        math.setDepartmentBossId(mathHead.getId());
        math.setDepartmentAssistantId(mathDeputy.getId());

        Student sara = student("student.sara", "Sara Tavakoli", StudentType.UNDERGRADUATE, cs, "23101001", 2023, karimi);
        Student omid = student("student.omid", "Omid Rostami", StudentType.UNDERGRADUATE, cs, "23101002", 2023, karimi);
        Student nika = student("student.nika", "Nika Parsa", StudentType.MASTERY, cs, "25102001", 2025, moradi);
        Student reza = student("student.reza", "Reza Kamali", StudentType.PHD, cs, "24103001", 2024, head);
        Student lena = student("student.lena", "Lena Ahmadi", StudentType.UNDERGRADUATE, math, "24201001", 2024, mathHead);

        Course fundamentals = course("Programming Fundamentals", "CS101", cs, karimi, CourseType.UNDER_GRADUATION, 4, 60, "Fall 2025", -300, "Sun 09:00-10:30", "Tue 09:00-10:30");
        Course structures = course("Data Structures", "CS201", cs, karimi, CourseType.UNDER_GRADUATION, 3, 2, "Fall 2026", 40, "Sun 10:30-12:00", "Tue 10:30-12:00");
        Course algorithms = course("Algorithms", "CS301", cs, moradi, CourseType.UNDER_GRADUATION, 3, 40, "Fall 2026", 42, "Mon 09:00-10:30", "Wed 09:00-10:30");
        Course databases = course("Databases", "CS320", cs, moradi, CourseType.UNDER_GRADUATION, 3, 35, "Fall 2026", 44, "Mon 13:30-15:00", "Wed 13:30-15:00");
        Course systems = course("Distributed Systems", "CS510", cs, head, CourseType.MASTERY, 3, 20, "Fall 2026", 45, "Thu 09:00-12:00");
        Course seminar = course("Research Seminar", "CS710", cs, head, CourseType.PHD, 2, 10, "Fall 2026", 46, "Thu 14:00-16:00");
        Course calculus = course("Calculus", "MA101", math, mathHead, CourseType.UNDER_GRADUATION, 4, 80, "Fall 2026", 41, "Sat 09:00-10:30", "Mon 10:30-12:00");
        structures.getCoursePrerequisites().add(fundamentals);
        algorithms.getCoursePrerequisites().add(structures);

        finished(sara, fundamentals, 18.25);
        finished(omid, fundamentals, 15.5);
        enroll(sara, structures);
        enroll(sara, calculus);
        enroll(omid, structures);
        Score pending = enroll(omid, databases);
        pending.setScoreType(ScoreType.TEMPORARY);
        pending.setScoreValue(16.75);
        enroll(nika, systems);
        enroll(reza, seminar);
        enroll(lena, calculus);
        nika.getStudentAssistingCourses().add(structures);

        EducationalContext notes = new EducationalContext();
        notes.setEducationalContextCourseId(structures.getCourseId());
        notes.setEducationalContextName("Week 1: Arrays and linked lists");
        notes.setEducationalContextText("Read chapters one and two before Sunday's class.");
        notes.setEducationalContextType(EducationalContextType.TEXT);
        notes.setEducationalContextTime(LocalDateTime.now().minusDays(9));
        DataManager.session().persist(notes);
        structures.getCourseEducationalContexts().add(notes);

        Exercise exercise = new Exercise();
        exercise.setExerciseCourseId(structures.getCourseId());
        exercise.setExerciseName("Homework 1: Linked lists");
        exercise.setExplanation("Implement a doubly linked list and submit your explanation as text.");
        exercise.setExerciseType(ExerciseType.TEXT);
        exercise.setExerciseStartTime(LocalDateTime.now().minusDays(5));
        exercise.setExerciseDeadline(LocalDateTime.now().plusDays(5));
        exercise.setExerciseEndTime(LocalDateTime.now().plusDays(7));
        DataManager.session().persist(exercise);
        structures.getCourseExercises().add(exercise);
        DataManager.addAnswer(omid.getId(), exercise.getExerciseId(), "Insertion and removal are constant time at either end.", null);

        Chat chat = DataManager.chatBetween(sara.getId(), omid.getId());
        DataManager.addMessage(omid.getId(), chat.getChatId(), "Did you start the linked list homework?", null);
        DataManager.addMessage(sara.getId(), chat.getChatId(), "Yes, the removal case took me a while.", null);
        DataManager.addRecommendationRequest(sara.getId(), karimi.getId());
        DataManager.addEnrollmentRequest(omid.getId());
    }

    private User user(String username, String fullName, UserType type) {
        User user = new User();
        user.setUsername(username);
        user.setUserFullName(fullName);
        user.setUserType(type);
        user.setUserPassword(passwordHash);
        user.setUserLastLoginTime(LocalDateTime.now());
        user.setUserEmail(username + "@portal.example");
        user.setUserPhoneNumber("0900000" + nextNational);
        user.setUserNationalNumber("000000" + nextNational++);
        user.setCanLogin(true);
        DataManager.session().persist(user);
        return user;
    }

    private Department department(DepartmentType type) {
        Department department = new Department();
        department.setDepartmentType(type);
        DataManager.session().persist(department);
        return department;
    }

    private Master master(String username, String name, UserType userType, MasterType type, MasterGrade grade,
                          Department department, String number, String room) {
        User user = user(username, name, userType);
        Master master = new Master();
        master.setUser(user);
        user.setMaster(master);
        master.setMasterType(type);
        master.setMasterGrade(grade);
        master.setMasterDepartmentId(department.getDepartmentId());
        master.setMasterShipNumber(number);
        master.setMasterRoomNumber(room);
        DataManager.session().persist(master);
        department.getDepartmentMasters().add(master);
        return master;
    }

    private Student student(String username, String name, StudentType type, Department department,
                            String number, int year, Master supervisor) {
        User user = user(username, name, UserType.STUDENT);
        Student student = new Student();
        student.setUser(user);
        user.setStudent(student);
        student.setStudentType(type);
        student.setStudentshipNumber(number);
        student.setEntryYear(year);
        student.setStudentDepartmentId(department.getDepartmentId());
        student.setStudentSupervisorId(supervisor.getId());
        student.setStudentRegistrationTime(LocalDateTime.now().minusHours(1));
        DataManager.session().persist(student);
        department.getDepartmentStudents().add(student);
        return student;
    }

    private Course course(String name, String number, Department department, Master master, CourseType type,
                          int units, int capacity, String term, int examInDays, String... classTimes) {
        Course course = new Course();
        course.setCourseName(name);
        course.setCourseNumber(number);
        course.setCourseDepartmentId(department.getDepartmentId());
        course.setCourseMasterId(master.getId());
        course.setCourseType(type);
        course.setCourseUnits(units);
        course.setCourseCapacity(capacity);
        course.setTerm(term);
        course.setCourseExamTime(today.plusDays(examInDays).atTime(9, 0));
        course.setCourseWeaklyPlan(new ArrayList<>(List.of(classTimes)));
        DataManager.session().persist(course);
        department.getDepartmentCourses().add(course);
        master.getMasterCourses().add(course);
        return course;
    }

    private Score enroll(Student student, Course course) {
        student.getStudentCourses().add(course);
        course.getCourseStudents().add(student);
        return DataManager.addScore(student, course);
    }

    private void finished(Student student, Course course, double value) {
        Score score = enroll(student, course);
        score.setScoreValue(value);
        score.setScoreType(ScoreType.VERIFIED);
    }
}
