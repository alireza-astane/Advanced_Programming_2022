package logic.pageBuilders;

import java.util.ArrayList;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.ClientHandler;
import serverDataBase.DataManager;
import network.Response;
import serverDataBase.models.Course;

public final class WeeklyPlanDataBuilder {

    private WeeklyPlanDataBuilder() {
    }

    public static WeeklyPlanData getWeeklyPlanData(ClientHandler clientHandler) {
        WeeklyPlanData weeklyPlanData =new WeeklyPlanData();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course:DataManager.coursesOf(clientHandler.getUser())){
            ArrayList<String> arrayList= new ArrayList<>();
            arrayList.add(course.getCourseName());
        
            arrayList.addAll(course.getCourseWeaklyPlan());
        
            arrayLists.add(arrayList);
        }
    
        weeklyPlanData.setWeeklyPlanList(arrayLists);
        return weeklyPlanData;
    }
}
