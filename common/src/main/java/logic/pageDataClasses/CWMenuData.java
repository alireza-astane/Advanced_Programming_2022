package logic.pageDataClasses;

import logic.enums.ResponseType;

import network.Response;


import java.util.ArrayList;

public class CWMenuData extends Response {
    public CWMenuData(){
        this.setResponseType(ResponseType.CW_MENU_DATA);
    }
    ArrayList<ArrayList<String>> coursesList;


    //list: course name course Id


    public ArrayList<ArrayList<String>> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(ArrayList<ArrayList<String>> coursesList) {
        this.coursesList = coursesList;
    }
}
