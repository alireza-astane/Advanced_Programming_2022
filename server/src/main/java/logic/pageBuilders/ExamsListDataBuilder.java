package logic.pageBuilders;

import java.util.ArrayList;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.ClientHandler;
import serverDataBase.DataManager;
import network.Response;
import serverDataBase.models.Course;

public final class ExamsListDataBuilder {

    private ExamsListDataBuilder() {
    }

    public static ExamsListData getExamsListData(ClientHandler clientHandler) {
        ExamsListData examsListData = new ExamsListData();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course:DataManager.coursesOf(clientHandler.getUser())){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(String.valueOf(course.getCourseId()));
            arrayList.add(course.getCourseName());
            arrayList.add(course.getCourseExamTime().toString());
            arrayLists.add(arrayList);
        }
        examsListData.setExamsList(arrayLists);
        return examsListData;
    }
}
