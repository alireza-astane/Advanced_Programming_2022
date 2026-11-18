package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.ClientHandler;
import network.Response;
import serverDataBase.models.Course;

import java.util.ArrayList;

public class WeeklyPlanData extends Response {

    public WeeklyPlanData(){
        super();
        this.setResponseType(ResponseType.WEEKLY_PLAN);
    }

    ArrayList<ArrayList<String>> weeklyPlanList;

    public static WeeklyPlanData getWeeklyPlanData(ClientHandler clientHandler) {
        WeeklyPlanData weeklyPlanData =new WeeklyPlanData();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        try {
            for(Course course:clientHandler.getUser().getStudent().getStudentCourses()){
                ArrayList<String> arrayList= new ArrayList<>();
                try {
                    arrayList.add(course.getCourseName());
                } catch (Exception ignored) {

                }
                try {
                    arrayList.addAll(course.getCourseWeaklyPlan());
                } catch (Exception ignored) {
                }
                arrayLists.add(arrayList);
            }
        } catch (Exception ignored) {
        }
        weeklyPlanData.setWeeklyPlanList(arrayLists);
        return weeklyPlanData;
    }


    public ArrayList<ArrayList<String>> getWeeklyPlanList() {
        return weeklyPlanList;
    }

    public void setWeeklyPlanList(ArrayList<ArrayList<String>> weeklyPlanList) {
        this.weeklyPlanList = weeklyPlanList;
    }

    // list: course Name,course Plan
}
