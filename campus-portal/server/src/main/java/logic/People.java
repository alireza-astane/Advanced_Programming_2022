package logic;

import database.DataManager;
import database.models.*;
import logic.enums.MasterType;
import logic.enums.ScoreType;
import logic.enums.UserType;
import network.Problem;

import java.util.List;

/** Questions about users and their standing that several services need. */
final class People {
    private People() {
    }

    static Student student(User user) {
        if (user.getStudent() == null) {
            throw new Problem("This page is for students.");
        }
        return user.getStudent();
    }

    static Master master(User user) {
        if (user.getMaster() == null) {
            throw new Problem("This page is for professors.");
        }
        return user.getMaster();
    }

    static boolean isHead(User user) {
        return user.getMaster() != null && user.getMaster().getMasterType() == MasterType.HEAD;
    }

    static boolean isDeputy(User user) {
        return user.getUserType() == UserType.ASSISTANT && user.getMaster() != null;
    }

    static List<Course> coursesOf(User user) {
        if (user.getStudent() != null) {
            return user.getStudent().getCourses();
        }
        if (user.getMaster() != null) {
            return DataManager.query(Course.class, "from Course where master = :p0", user.getMaster());
        }
        return List.of();
    }

    static List<Score> scoresOf(Student student) {
        return DataManager.query(Score.class, "from Score where student = :p0", student);
    }

    static List<Score> finalScores(Student student) {
        return scoresOf(student).stream().filter(score -> score.getScoreType() == ScoreType.FINAL).toList();
    }

    static boolean passed(Score score) {
        return score.getScoreValue() >= Configuration.number("passScore");
    }

    static double average(Student student) {
        double points = 0;
        int units = 0;
        for (Score score : finalScores(student)) {
            points += score.getScoreValue() * score.getCourse().getUnits();
            units += score.getCourse().getUnits();
        }
        return units == 0 ? 0 : points / units;
    }

    static List<Course> passedCourses(Student student) {
        return finalScores(student).stream().filter(People::passed).map(Score::getCourse).toList();
    }

    static Student studentByNumber(String number) {
        Student student = DataManager.first(Student.class, "from Student where studentNumber = :p0", number);
        if (student == null) {
            throw new Problem("No student has the number " + number + ".");
        }
        return student;
    }
}
