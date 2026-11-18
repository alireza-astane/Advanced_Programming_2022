package logic.pageDataClasses;

import logic.enums.CourseType;
import logic.enums.ScoreType;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.Score;

import java.util.ArrayList;
@Getter
@Setter
public class SuggestedCoursesData extends Response {
    ArrayList<ArrayList<String>> suggestedCourseList;  //should be filtered

    public static SuggestedCoursesData getSuggestedCoursesData(ClientHandler clientHandler) {
        //todo filter not allowed
        SuggestedCoursesData suggestedCoursesData =new SuggestedCoursesData();
        ArrayList<Course> courses = DataManager.getCourses();
        courses.removeIf(course -> course.getCourseCapacity()==course.getCourseStudents().size());
        switch (clientHandler.getUser().getStudent().getStudentType()){
            case UNDERGRADUATE -> courses.removeIf(course -> course.getCourseType()!= CourseType.UNDER_GRADUATION);
            case MASTERY -> courses.removeIf(course -> course.getCourseType()!=CourseType.MASTERY);
            case PHD -> courses.removeIf(course -> course.getCourseType()!=CourseType.PHD);
        }



        ArrayList<Course> studentPassedCourse = (ArrayList<Course>) clientHandler.getUser().getStudent().getStudentCourses();
        for(Course course:studentPassedCourse){
            for(Score score:course.getScores()){
                if(score.getScoreStudentId()==clientHandler.getUser().getId()){
                    if(score.getScoreType()== ScoreType.VERIFIED){
                        if(score.getScoreValue()<10){
                            studentPassedCourse.remove(course);
                        }
                    }

                }
            }
        }
        courses.removeIf(course -> !studentPassedCourse.contains(course));


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

    public ArrayList<ArrayList<String>> getSuggestedCourseList() {
        return suggestedCourseList;
    }

    public void setSuggestedCourseList(ArrayList<ArrayList<String>> suggestedCourseList) {
        this.suggestedCourseList = suggestedCourseList;
    }
    // examTime name grade courseId

}
