package logic.pageBuilders;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import logic.enums.CourseType;
import logic.enums.ScoreType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.*;
import serverDataBase.models.Course;
import serverDataBase.models.Score;

public final class SuggestedCoursesDataBuilder {

    private SuggestedCoursesDataBuilder() {
    }

    public static SuggestedCoursesData getSuggestedCoursesData(ClientHandler clientHandler) {
        SuggestedCoursesData suggestedCoursesData =new SuggestedCoursesData();
        ArrayList<Course> courses = new ArrayList<>(DataManager.getCourses());
        courses.removeIf(course -> course.getCourseCapacity()==course.getCourseStudents().size());
        switch (clientHandler.getUser().getStudent().getStudentType()){
            case UNDERGRADUATE -> courses.removeIf(course -> course.getCourseType()!= CourseType.UNDER_GRADUATION);
            case MASTERY -> courses.removeIf(course -> course.getCourseType()!=CourseType.MASTERY);
            case PHD -> courses.removeIf(course -> course.getCourseType()!=CourseType.PHD);
        }



        Student student = clientHandler.getUser().getStudent();
        Set<Course> passed = new HashSet<>();
        for(Score score:student.getStudentScores()){
            if(score.getScoreType()== ScoreType.VERIFIED && score.getScoreValue()>=10){
                passed.add(DataManager.getCourseById(score.getScoreCourseId()));
            }
        }
        courses.removeIf(course -> passed.contains(course) || student.getStudentCourses().contains(course)
                || !passed.containsAll(course.getCoursePrerequisites()));


        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course:courses){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(String.valueOf(course.getCourseId()));
            arrayList.add(course.getCourseName());
            arrayList.add(course.getCourseType().toString());
            arrayList.add(course.getCourseExamTime().toString());

            arrayLists.add(arrayList);
        }

        suggestedCoursesData.setSuggestedCourseList(arrayLists);

        return suggestedCoursesData;

    }
}
