package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.EducationalContext;
import serverDataBase.models.Exercise;

import java.util.ArrayList;
import java.util.Arrays;

public class CWCoursePageData extends Response {
    public CWCoursePageData(){
        this.setResponseType(ResponseType.CW_COURSE_PAGE_DATA);
    }
    String examTime;  //id name time;
    ArrayList<ArrayList<String>> educationalContexts; //id name time
    ArrayList<ArrayList<String>> exercises;  // name time

    public static CWCoursePageData getCWCourseData(int courseId) {
        CWCoursePageData cwCoursePageData = new CWCoursePageData();
        Course course = DataManager.getCourseById(courseId);

        cwCoursePageData.setExamTime(course.getCourseExamTime().toString());

        ArrayList<ArrayList<String>> arrayLists2 = new ArrayList<>();
        for(Exercise exercise:course.getCourseExercises()){
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(String.valueOf(exercise.getExerciseId()),exercise.getExerciseName(),exercise.getExerciseDeadline().toString()));
            arrayLists2.add(arrayList);

        }
        cwCoursePageData.setExercises(arrayLists2);

        ArrayList<ArrayList<String>> arrayLists3 = new ArrayList<>();
        for(EducationalContext educationalContext:course.getCourseEducationalContexts()){
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(String.valueOf(educationalContext.getEducationalContextId()),educationalContext.getEducationalContextName(),educationalContext.getEducationalContextTime().toString()));
            arrayLists3.add(arrayList);
        }
        cwCoursePageData.setEducationalContexts(arrayLists3);
        return cwCoursePageData;

    }

    public ArrayList<ArrayList<String>> getEducationalContexts() {
        return educationalContexts;
    }

    public ArrayList<ArrayList<String>> getExercises() {
        return exercises;
    }

    public void setEducationalContexts(ArrayList<ArrayList<String>> educationalContexts) {
        this.educationalContexts = educationalContexts;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExercises(ArrayList<ArrayList<String>> exercises) {
        this.exercises = exercises;
    }
}
