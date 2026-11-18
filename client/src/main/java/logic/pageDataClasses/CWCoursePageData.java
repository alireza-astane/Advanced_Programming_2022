package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;


import java.util.ArrayList;
import java.util.Arrays;

public class CWCoursePageData extends Response {
    public CWCoursePageData(){
        this.setResponseType(ResponseType.CW_COURSE_PAGE_DATA);
    }
    String examTime;  //id name time;
    ArrayList<ArrayList<String>> educationalContexts; //id name time
    ArrayList<ArrayList<String>> exercises;  // name time


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
