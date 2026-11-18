package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.Student;

import java.util.ArrayList;

public class CWMenuData extends Response {
    public CWMenuData(){
        this.setResponseType(ResponseType.CW_MENU_DATA);
    }
    ArrayList<ArrayList<String>> coursesList;

    public static CWMenuData getCwMenuData(ClientHandler clientHandler) {
        CWMenuData cwMenuData =new CWMenuData();
        Student student = clientHandler.getUser().getStudent();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course: student.getStudentCourses()){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(String.valueOf(course.getCourseId()));
            arrayList.add(String.valueOf(course.getCourseName()));
            arrayLists.add(arrayList);
        }
        cwMenuData.setCoursesList(arrayLists);

        return cwMenuData;
    }
    //list: course name course Id


    public ArrayList<ArrayList<String>> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(ArrayList<ArrayList<String>> coursesList) {
        this.coursesList = coursesList;
    }
}
