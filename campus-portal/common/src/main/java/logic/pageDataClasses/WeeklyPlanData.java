package logic.pageDataClasses;

import logic.enums.ResponseType;

import network.Response;


import java.util.ArrayList;

public class WeeklyPlanData extends Response {

    public WeeklyPlanData(){
        super();
        this.setResponseType(ResponseType.WEEKLY_PLAN);
    }

    ArrayList<ArrayList<String>> weeklyPlanList;



    public ArrayList<ArrayList<String>> getWeeklyPlanList() {
        return weeklyPlanList;
    }

    public void setWeeklyPlanList(ArrayList<ArrayList<String>> weeklyPlanList) {
        this.weeklyPlanList = weeklyPlanList;
    }

    // list: course Name,course Plan
}
