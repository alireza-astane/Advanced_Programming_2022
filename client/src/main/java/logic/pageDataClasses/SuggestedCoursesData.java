package logic.pageDataClasses;

import logic.enums.CourseType;
import logic.enums.ScoreType;


import network.Response;

import java.util.ArrayList;

public class SuggestedCoursesData extends Response {
    ArrayList<ArrayList<String>> suggestedCourseList;  //should be filtered


    public ArrayList<ArrayList<String>> getSuggestedCourseList() {
        return suggestedCourseList;
    }

    public void setSuggestedCourseList(ArrayList<ArrayList<String>> suggestedCourseList) {
        this.suggestedCourseList = suggestedCourseList;
    }
    // examTime name grade courseId

}
