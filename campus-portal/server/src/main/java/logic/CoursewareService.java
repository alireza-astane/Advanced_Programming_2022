package logic;

import database.DataManager;
import database.models.*;
import logic.enums.ExerciseType;
import network.Call;
import network.Problem;
import network.Protocol;
import network.RequestType;
import network.Response;
import network.Routes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CoursewareService {
    private enum Role { MASTER, ASSISTANT, STUDENT }

    private CoursewareService() {
    }

    public static void register(Routes routes) {
        routes.on(RequestType.CW_COURSES, Routes.UNIVERSITY, CoursewareService::courses);
        routes.on(RequestType.CW_COURSE, Routes.UNIVERSITY, CoursewareService::course);
        routes.on(RequestType.CW_CALENDAR, Routes.UNIVERSITY, CoursewareService::calendar);
        routes.on(RequestType.CW_ADD_MEMBER, Routes.STAFF, CoursewareService::addMember);
        routes.on(RequestType.CW_ADD_CONTENT, Routes.STAFF, CoursewareService::addContent);
        routes.on(RequestType.CW_DELETE_CONTENT, Routes.STAFF, CoursewareService::deleteContent);
        routes.on(RequestType.CW_CONTENT, Routes.UNIVERSITY, CoursewareService::content);
        routes.on(RequestType.CW_SAVE_ITEM, Routes.UNIVERSITY, CoursewareService::saveItem);
        routes.on(RequestType.CW_DELETE_ITEM, Routes.STAFF, CoursewareService::deleteItem);
        routes.on(RequestType.CW_ITEM_FILE, Routes.UNIVERSITY, CoursewareService::itemFile);
        routes.on(RequestType.CW_ADD_EXERCISE, Routes.STAFF, CoursewareService::addExercise);
        routes.on(RequestType.CW_EXERCISE, Routes.UNIVERSITY, CoursewareService::exercise);
        routes.on(RequestType.CW_EXERCISE_FILE, Routes.UNIVERSITY, CoursewareService::exerciseFile);
        routes.on(RequestType.CW_SUBMIT_ANSWER, Routes.STUDENT, CoursewareService::submit);
        routes.on(RequestType.CW_ANSWER_FILE, Routes.UNIVERSITY, CoursewareService::answerFile);
        routes.on(RequestType.CW_GRADE_ANSWER, Routes.UNIVERSITY, CoursewareService::grade);
    }

    private static Role roleIn(User user, Course course) {
        if (user.getMaster() != null && course.getMaster() == user.getMaster()) {
            return Role.MASTER;
        }
        if (user.getStudent() != null && user.getStudent().getAssistingCourses().contains(course)) {
            return Role.ASSISTANT;
        }
        if (user.getStudent() != null && user.getStudent().getCourses().contains(course)) {
            return Role.STUDENT;
        }
        throw new Problem("You are not part of that course.");
    }

    private static void requireMaster(Call call, Course course) {
        if (roleIn(call.user(), course) != Role.MASTER) {
            throw new Problem("Only the course's professor can do that.");
        }
    }

    private static void requireStaff(Call call, Course course) {
        if (roleIn(call.user(), course) == Role.STUDENT) {
            throw new Problem("Only course staff can do that.");
        }
    }

    private static Set<Course> myCourses(User user) {
        Set<Course> courses = new LinkedHashSet<>(People.coursesOf(user));
        if (user.getStudent() != null) {
            courses.addAll(user.getStudent().getAssistingCourses());
        }
        return courses;
    }

    private static Response courses(Call call) {
        Response response = new Response();
        for (Course course : myCourses(call.user())) {
            response.row("courses").put("id", course.getId()).put("name", course.getName())
                    .put("number", course.getCourseNumber()).put("role", roleIn(call.user(), course))
                    .put("master", course.getMaster().getUser().getFullName());
        }
        return response;
    }

    private static List<Exercise> exercisesOf(Course course) {
        return DataManager.query(Exercise.class, "from Exercise where course = :p0 order by freeUntil", course);
    }

    private static Response course(Call call) {
        Course course = DataManager.get(Course.class, call.id(0));
        Role role = roleIn(call.user(), course);
        Response response = new Response().put("name", course.getName()).put("role", role)
                .put("examTime", Format.time(course.getExamTime()));
        for (CourseContent content : DataManager.query(CourseContent.class,
                "from CourseContent where course = :p0 order by createdAt desc", course)) {
            response.row("contents").put("id", content.getId()).put("name", content.getName())
                    .put("time", Format.time(content.getCreatedAt()));
        }
        addExerciseRows(response, course, role);
        if (role == Role.MASTER) {
            addMemberRows(response, course);
        }
        return response;
    }

    private static void addExerciseRows(Response response, Course course, Role role) {
        LocalDateTime now = LocalDateTime.now();
        for (Exercise exercise : exercisesOf(course)) {
            if (role != Role.STUDENT || !now.isBefore(exercise.getOpensAt())) {
                response.row("exercises").put("id", exercise.getId()).put("name", exercise.getName())
                        .put("opensAt", Format.time(exercise.getOpensAt()))
                        .put("freeUntil", Format.time(exercise.getFreeUntil()))
                        .put("closesAt", Format.time(exercise.getClosesAt()));
            }
        }
    }

    private static void addMemberRows(Response response, Course course) {
        for (Student student : course.getStudents()) {
            memberRow(response, student, "Student");
        }
        for (Student student : course.getAssistants()) {
            memberRow(response, student, "Teaching assistant");
        }
    }

    private static void memberRow(Response response, Student student, String role) {
        response.row("members").put("name", student.getUser().getFullName())
                .put("number", student.getStudentNumber()).put("role", role);
    }

    private static Response calendar(Call call) {
        record Entry(String course, String title, LocalDateTime time) {
        }
        List<Entry> entries = new ArrayList<>();
        for (Course course : myCourses(call.user())) {
            entries.add(new Entry(course.getName(), "Final exam", course.getExamTime()));
            for (Exercise exercise : exercisesOf(course)) {
                entries.add(new Entry(course.getName(), exercise.getName(), exercise.getFreeUntil()));
            }
        }
        entries.sort(Comparator.comparing(Entry::time));
        Response response = new Response();
        entries.forEach(entry -> response.row("entries").put("course", entry.course()).put("title", entry.title())
                .put("time", Format.time(entry.time())));
        return response;
    }

    private static Response addMember(Call call) {
        Course course = DataManager.get(Course.class, call.id(0));
        requireMaster(call, course);
        Student student = People.studentByNumber(call.required(1, "Student number"));
        boolean assistant = Boolean.parseBoolean(call.text(2));
        if (assistant) {
            if (!student.getAssistingCourses().contains(course)) {
                student.getAssistingCourses().add(course);
            }
        } else {
            UnitSelectionService.enroll(student, course);
        }
        EducationService.notice(student.getUser(),
                Configuration.label(assistant ? "addedAsAssistant" : "addedAsStudent", course.getName()));
        return null;
    }

    private static Response addContent(Call call) {
        Course course = DataManager.get(Course.class, call.id(0));
        requireMaster(call, course);
        CourseContent content = new CourseContent();
        content.setCourse(course);
        content.setName(call.required(1, "Name"));
        DataManager.save(content);
        return new Response().put("id", content.getId());
    }

    private static Response deleteContent(Call call) {
        CourseContent content = DataManager.get(CourseContent.class, call.id(0));
        requireMaster(call, content.getCourse());
        DataManager.delete(content);
        return null;
    }

    private static Response content(Call call) {
        CourseContent content = DataManager.get(CourseContent.class, call.id(0));
        Role role = roleIn(call.user(), content.getCourse());
        Response response = new Response().put("name", content.getName()).put("role", role);
        for (ContentItem item : content.getItems()) {
            response.row("items").put("id", item.getId()).put("text", item.getText())
                    .put("fileName", item.getFileName());
        }
        return response;
    }

    private static void checkFile(byte[] file) {
        if (file != null && file.length > Protocol.MAX_FILE_BYTES) {
            throw new Problem("That file is too large.");
        }
    }

    /** Arguments: content, item (empty when new), text, file name. */
    private static Response saveItem(Call call) {
        CourseContent content = DataManager.get(CourseContent.class, call.id(0));
        checkFile(call.file());
        ContentItem item = call.text(1).isEmpty() ? newItem(call, content) : existingItem(call, content);
        item.setText(call.text(2));
        if (call.file() != null) {
            item.setFile(call.file());
            item.setFileName(call.required(3, "File name"));
        }
        if (item.getText().isBlank() && item.getFileName() == null) {
            throw new Problem("Add some text or a file.");
        }
        return null;
    }

    private static ContentItem newItem(Call call, CourseContent content) {
        requireMaster(call, content.getCourse());
        if (content.getItems().size() >= Configuration.number("contentItemLimit")) {
            throw new Problem("A page can hold at most " + Configuration.number("contentItemLimit") + " items.");
        }
        ContentItem item = new ContentItem();
        item.setContent(content);
        content.getItems().add(item);
        DataManager.save(item);
        return item;
    }

    private static ContentItem existingItem(Call call, CourseContent content) {
        requireStaff(call, content.getCourse());
        ContentItem item = DataManager.get(ContentItem.class, call.id(1));
        if (item.getContent() != content) {
            throw new Problem("That item belongs to another page.");
        }
        return item;
    }

    private static Response deleteItem(Call call) {
        ContentItem item = DataManager.get(ContentItem.class, call.id(0));
        requireMaster(call, item.getContent().getCourse());
        item.getContent().getItems().remove(item);
        return null;
    }

    private static Response itemFile(Call call) {
        ContentItem item = DataManager.get(ContentItem.class, call.id(0));
        roleIn(call.user(), item.getContent().getCourse());
        return new Response().put("fileName", item.getFileName()).setFile(item.getFile());
    }

    /** Arguments: course, name, description, opens, closes, free until, answer type, file name. */
    private static Response addExercise(Call call) {
        Course course = DataManager.get(Course.class, call.id(0));
        requireMaster(call, course);
        checkFile(call.file());
        Exercise exercise = new Exercise();
        exercise.setCourse(course);
        exercise.setName(call.required(1, "Name"));
        exercise.setDescription(call.text(2));
        exercise.setOpensAt(call.time(3, "Opening time"));
        exercise.setClosesAt(call.time(4, "Closing time"));
        exercise.setFreeUntil(call.time(5, "Free submission time"));
        if (!exercise.getOpensAt().isBefore(exercise.getFreeUntil())
                || exercise.getFreeUntil().isAfter(exercise.getClosesAt())) {
            throw new Problem("The times must run from opening, to free submission, to closing.");
        }
        exercise.setAnswerType(ExerciseType.valueOf(call.required(6, "Answer type")));
        exercise.setFile(call.file());
        exercise.setFileName(call.file() == null ? null : call.required(7, "File name"));
        DataManager.save(exercise);
        return new Response().put("id", exercise.getId());
    }

    private static Response exercise(Call call) {
        Exercise exercise = DataManager.get(Exercise.class, call.id(0));
        Role role = roleIn(call.user(), exercise.getCourse());
        if (role == Role.STUDENT && LocalDateTime.now().isBefore(exercise.getOpensAt())) {
            throw new Problem("This exercise has not opened yet.");
        }
        Response response = new Response().put("name", exercise.getName()).put("role", role)
                .put("description", exercise.getDescription()).put("answerType", exercise.getAnswerType())
                .put("opensAt", Format.time(exercise.getOpensAt())).put("freeUntil", Format.time(exercise.getFreeUntil()))
                .put("closesAt", Format.time(exercise.getClosesAt())).put("fileName", exercise.getFileName());
        if (role == Role.STUDENT) {
            addOwnAnswer(response, exercise, call.user().getStudent());
        } else {
            exercise.getAnswers().forEach(answer -> answerRow(response, answer, role == Role.ASSISTANT));
        }
        return response;
    }

    private static Answer answerOf(Exercise exercise, Student student) {
        return exercise.getAnswers().stream().filter(answer -> answer.getStudent() == student).findFirst().orElse(null);
    }

    private static void addOwnAnswer(Response response, Exercise exercise, Student student) {
        Answer answer = answerOf(exercise, student);
        response.put("submitted", answer != null);
        if (answer != null) {
            response.put("answerId", answer.getId()).put("answerText", answer.getText())
                    .put("answerFile", answer.getFileName()).put("uploadedAt", Format.time(answer.getUploadedAt()))
                    .put("grade", answer.getGrade() == null ? "" : Format.score(answer.getGrade()));
        }
    }

    private static void answerRow(Response response, Answer answer, boolean masked) {
        User user = answer.getStudent().getUser();
        response.row("answers").put("id", answer.getId())
                .put("student", masked ? mask(user.getFullName()) : user.getFullName())
                .put("number", masked ? mask(answer.getStudent().getStudentNumber()) : answer.getStudent().getStudentNumber())
                .put("time", Format.time(answer.getUploadedAt())).put("text", answer.getText())
                .put("fileName", answer.getFileName())
                .put("grade", answer.getGrade() == null ? "" : Format.score(answer.getGrade()));
    }

    private static String mask(String text) {
        return "*".repeat(text.length());
    }

    private static Response exerciseFile(Call call) {
        Exercise exercise = DataManager.get(Exercise.class, call.id(0));
        roleIn(call.user(), exercise.getCourse());
        return new Response().put("fileName", exercise.getFileName()).setFile(exercise.getFile());
    }

    /** Arguments: exercise, text, file name. */
    private static Response submit(Call call) {
        Exercise exercise = DataManager.get(Exercise.class, call.id(0));
        if (roleIn(call.user(), exercise.getCourse()) != Role.STUDENT) {
            throw new Problem("Only students of the course can submit answers.");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exercise.getOpensAt()) || now.isAfter(exercise.getClosesAt())) {
            throw new Problem("This exercise is not open.");
        }
        checkFile(call.file());
        checkAnswerType(call, exercise);
        storeAnswer(call, exercise, now);
        return null;
    }

    private static void checkAnswerType(Call call, Exercise exercise) {
        if (exercise.getAnswerType() == ExerciseType.TEXT && call.text(1).isEmpty()) {
            throw new Problem("This exercise needs a written answer.");
        }
        if (exercise.getAnswerType() == ExerciseType.MEDIA && call.file() == null) {
            throw new Problem("This exercise needs an uploaded file.");
        }
    }

    private static void storeAnswer(Call call, Exercise exercise, LocalDateTime now) {
        Student student = call.user().getStudent();
        Answer answer = answerOf(exercise, student);
        if (answer == null) {
            answer = new Answer();
            answer.setExercise(exercise);
            answer.setStudent(student);
            exercise.getAnswers().add(answer);
            DataManager.save(answer);
        }
        answer.setUploadedAt(now);
        answer.setText(call.text(1));
        answer.setFile(call.file());
        answer.setFileName(call.file() == null ? null : call.required(2, "File name"));
    }

    private static Response answerFile(Call call) {
        Answer answer = DataManager.get(Answer.class, call.id(0));
        Course course = answer.getExercise().getCourse();
        if (answer.getStudent().getUser() != call.user()) {
            requireStaff(call, course);
        }
        return new Response().put("fileName", answer.getFileName()).setFile(answer.getFile());
    }

    private static Response grade(Call call) {
        Answer answer = DataManager.get(Answer.class, call.id(0));
        requireStaff(call, answer.getExercise().getCourse());
        double grade = call.decimal(1, "Grade");
        if (grade < 0) {
            throw new Problem("Grades cannot be negative.");
        }
        answer.setGrade(grade);
        return null;
    }
}
