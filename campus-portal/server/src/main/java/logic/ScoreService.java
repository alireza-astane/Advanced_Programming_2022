package logic;

import database.DataManager;
import database.models.*;
import logic.enums.ScoreType;
import network.Call;
import network.Problem;
import network.RequestType;
import network.Response;
import network.Routes;

import java.util.List;

public final class ScoreService {
    private static final double MAX_SCORE = 20;

    private ScoreService() {
    }

    public static void register(Routes routes) {
        routes.on(RequestType.MY_TEMPORARY_SCORES, Routes.STUDENT, ScoreService::myTemporaryScores);
        routes.on(RequestType.PROTEST_SCORE, Routes.STUDENT, ScoreService::protest);
        routes.on(RequestType.TEACHING_COURSES, Routes.STAFF, ScoreService::teachingCourses);
        routes.on(RequestType.COURSE_SCORES, Routes.STAFF, ScoreService::courseScores);
        routes.on(RequestType.SET_SCORE, Routes.STAFF, ScoreService::setScore);
        routes.on(RequestType.ANSWER_PROTEST, Routes.STAFF, ScoreService::answerProtest);
        routes.on(RequestType.PUBLISH_SCORES, Routes.STAFF, ScoreService::publish);
        routes.on(RequestType.FINALIZE_SCORES, Routes.STAFF, ScoreService::finalizeScores);
        routes.on(RequestType.DEPARTMENT_SCORES, Routes.DEPUTY, ScoreService::departmentScores);
        routes.on(RequestType.TRANSCRIPT, Routes.STUDENT, call -> transcript(People.student(call.user())));
        routes.on(RequestType.STUDENT_TRANSCRIPT, Routes.DEPUTY, ScoreService::studentTranscript);
        routes.on(RequestType.DEPARTMENT_STUDENTS, Routes.DEPUTY, ScoreService::departmentStudents);
    }

    private static List<Score> scoresOf(Course course) {
        return DataManager.query(Score.class, "from Score where course = :p0 order by student.studentNumber", course);
    }

    static void createScore(Student student, Course course) {
        Score score = new Score();
        score.setStudent(student);
        score.setCourse(course);
        DataManager.save(score);
    }

    private static Response myTemporaryScores(Call call) {
        Response response = new Response();
        for (Score score : People.scoresOf(People.student(call.user()))) {
            if (score.getScoreType() == ScoreType.TEMPORARY) {
                response.row("scores").put("id", score.getId()).put("course", score.getCourse().getName())
                        .put("value", Format.score(score.getScoreValue())).put("protest", score.getProtest())
                        .put("answer", score.getProtestAnswer());
            }
        }
        return response;
    }

    private static Response protest(Call call) {
        Score score = DataManager.get(Score.class, call.id(0));
        if (score.getStudent().getUser() != call.user() || score.getScoreType() != ScoreType.TEMPORARY) {
            throw new Problem("You cannot object to that grade.");
        }
        score.setProtest(call.required(1, "Objection"));
        return null;
    }

    private static Response teachingCourses(Call call) {
        Response response = new Response();
        for (Course course : People.coursesOf(call.user())) {
            response.row("courses").put("id", course.getId()).put("name", course.getName())
                    .put("number", course.getCourseNumber()).put("term", course.getTerm());
        }
        return response;
    }

    private static Course ownCourse(Call call, int courseId) {
        Course course = DataManager.get(Course.class, courseId);
        if (course.getMaster() != call.user().getMaster()) {
            throw new Problem("You do not teach that course.");
        }
        return course;
    }

    private static Score ownScore(Call call) {
        Score score = DataManager.get(Score.class, call.id(0));
        ownCourse(call, score.getCourse().getId());
        return score;
    }

    private static Response courseScores(Call call) {
        Course course = ownCourse(call, call.id(0));
        Response response = new Response().put("course", course.getName());
        boolean allEntered = true;
        ScoreType stage = ScoreType.FINAL;
        for (Score score : scoresOf(course)) {
            allEntered &= score.isEntered();
            stage = score.getScoreType().ordinal() < stage.ordinal() ? score.getScoreType() : stage;
            scoreRow(response, score);
        }
        return response.put("allEntered", allEntered).put("stage", stage);
    }

    private static void scoreRow(Response response, Score score) {
        User user = score.getStudent().getUser();
        response.row("scores").put("id", score.getId()).put("student", user.getFullName())
                .put("number", score.getStudent().getStudentNumber())
                .put("value", score.isEntered() ? Format.score(score.getScoreValue()) : "")
                .put("state", score.getScoreType()).put("protest", score.getProtest())
                .put("answer", score.getProtestAnswer());
    }

    private static void applyValue(Score score, double value) {
        if (score.getScoreType() == ScoreType.FINAL) {
            throw new Problem("Final grades cannot be changed.");
        }
        if (value < 0 || value > MAX_SCORE) {
            throw new Problem("Grades must be between 0 and 20.");
        }
        score.setScoreValue(Math.round(value * 4) / 4.0);
        score.setEntered(true);
    }

    private static Response setScore(Call call) {
        applyValue(ownScore(call), call.decimal(1, "Grade"));
        return null;
    }

    private static Response answerProtest(Call call) {
        Score score = ownScore(call);
        score.setProtestAnswer(call.required(1, "Answer"));
        if (!call.text(2).isEmpty()) {
            applyValue(score, call.decimal(2, "Grade"));
        }
        return null;
    }

    private static Response publish(Call call) {
        List<Score> scores = scoresOf(ownCourse(call, call.id(0)));
        if (scores.isEmpty() || !scores.stream().allMatch(Score::isEntered)) {
            throw new Problem("Enter a grade for every student first.");
        }
        scores.stream().filter(score -> score.getScoreType() == ScoreType.NOT_PUBLISHED)
                .forEach(score -> score.setScoreType(ScoreType.TEMPORARY));
        return null;
    }

    private static Response finalizeScores(Call call) {
        List<Score> scores = scoresOf(ownCourse(call, call.id(0)));
        if (scores.isEmpty() || scores.stream().anyMatch(score -> score.getScoreType() == ScoreType.NOT_PUBLISHED)) {
            throw new Problem("Publish the temporary grades first.");
        }
        scores.forEach(score -> score.setScoreType(ScoreType.FINAL));
        return null;
    }

    private static Response departmentScores(Call call) {
        Response response = new Response();
        for (Course course : DataManager.query(Course.class, "from Course where department = :p0 order by courseNumber",
                People.master(call.user()).getDepartment())) {
            List<Score> scores = scoresOf(course);
            for (Score score : scores) {
                if (score.getScoreType() != ScoreType.NOT_PUBLISHED) {
                    publishedRow(response, course, score);
                }
            }
            summaryRow(response, course, scores);
        }
        return response;
    }

    private static void publishedRow(Response response, Course course, Score score) {
        response.row("scores").put("course", course.getName())
                .put("master", course.getMaster().getUser().getFullName())
                .put("student", score.getStudent().getUser().getFullName())
                .put("number", score.getStudent().getStudentNumber())
                .put("value", Format.score(score.getScoreValue())).put("state", score.getScoreType())
                .put("protest", score.getProtest()).put("answer", score.getProtestAnswer());
    }

    private static void summaryRow(Response response, Course course, List<Score> scores) {
        List<Double> all = scores.stream().filter(score -> score.getScoreType() == ScoreType.FINAL)
                .map(Score::getScoreValue).toList();
        double pass = Configuration.number("passScore");
        List<Double> passed = all.stream().filter(value -> value >= pass).toList();
        response.row("summaries").put("course", course.getName()).put("average", mean(all))
                .put("passed", passed.size()).put("failed", all.size() - passed.size())
                .put("averageOfPassed", mean(passed));
    }

    private static String mean(List<Double> values) {
        return values.isEmpty() ? "N/A"
                : Format.score(values.stream().mapToDouble(Double::doubleValue).average().orElse(0));
    }

    static Response transcript(Student student) {
        Response response = new Response().put("student", student.getUser().getFullName())
                .put("number", student.getStudentNumber())
                .put("average", Format.score(People.average(student)));
        int passedUnits = 0;
        for (Score score : People.scoresOf(student)) {
            boolean isFinal = score.getScoreType() == ScoreType.FINAL;
            Course course = score.getCourse();
            response.row("courses").put("course", course.getName()).put("term", course.getTerm())
                    .put("units", course.getUnits())
                    .put("value", isFinal ? Format.score(score.getScoreValue()) : "N/A");
            passedUnits += isFinal && People.passed(score) ? course.getUnits() : 0;
        }
        return response.put("passedUnits", passedUnits);
    }

    private static Response studentTranscript(Call call) {
        Student student = DataManager.get(Student.class, call.id(0));
        if (student.getDepartment() != People.master(call.user()).getDepartment()) {
            throw new Problem("That student belongs to another department.");
        }
        return transcript(student);
    }

    private static Response departmentStudents(Call call) {
        Response response = new Response();
        for (Student student : DataManager.query(Student.class,
                "from Student where department = :p0 order by studentNumber", People.master(call.user()).getDepartment())) {
            response.row("students").put("id", student.getId()).put("name", student.getUser().getFullName())
                    .put("number", student.getStudentNumber()).put("entryYear", student.getEntryYear())
                    .put("level", student.getStudentType());
        }
        return response;
    }
}
