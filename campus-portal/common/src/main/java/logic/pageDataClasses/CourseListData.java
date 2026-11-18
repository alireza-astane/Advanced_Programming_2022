package logic.pageDataClasses;

import logic.enums.CourseType;
import logic.enums.ResponseType;

import network.Response;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;


public class CourseListData extends Response {
    ArrayList<ArrayList<String>> courseList;




    public ArrayList<ArrayList<String>> getCourseList() {
        return courseList;
    }

    public void setCourseList(ArrayList<ArrayList<String>> courseList) {
        this.courseList = courseList;
    }
//arrayList :
    //num
    //            name
    //            masterName
    //            units
    //            departmentName
    //            plan?
    //            examTime






}
