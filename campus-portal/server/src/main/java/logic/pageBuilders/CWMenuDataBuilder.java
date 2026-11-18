package logic.pageBuilders;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.ArrayList;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.ClientHandler;
import serverDataBase.models.*;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.Student;

public final class CWMenuDataBuilder {

    private CWMenuDataBuilder() {
    }

    public static CWMenuData getCwMenuData(ClientHandler clientHandler) {
        CWMenuData cwMenuData =new CWMenuData();
        User user = clientHandler.getUser();
        Set<Course> courses = new LinkedHashSet<>(DataManager.coursesOf(user));
        if(user.getStudent()!=null){
            courses.addAll(user.getStudent().getStudentAssistingCourses());
        }
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course: courses){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(String.valueOf(course.getCourseId()));
            arrayList.add(String.valueOf(course.getCourseName()));
            arrayLists.add(arrayList);
        }
        cwMenuData.setCoursesList(arrayLists);

        return cwMenuData;
    }
}
