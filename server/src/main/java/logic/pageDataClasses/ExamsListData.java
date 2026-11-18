package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.ClientHandler;
import network.Response;
import serverDataBase.models.Course;

import java.util.ArrayList;

public class ExamsListData extends Response {
    public ExamsListData(){
        super();
        this.setResponseType(ResponseType.EXAMS_TIME);
    }

    ArrayList<ArrayList<String>> examsList;

    public static ExamsListData getExamsListData(ClientHandler clientHandler) {
        ExamsListData examsListData = new ExamsListData();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Course course:clientHandler.getUser().getStudent().getStudentCourses()){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(String.valueOf(course.getCourseId()));
            arrayList.add(course.getCourseName());
            arrayList.add(course.getCourseExamTime().toString());
            arrayLists.add(arrayList);
        }
        examsListData.setExamsList(arrayLists);
        return examsListData;
    }


    //ArrayList:   courseId,courseName,examTime


    public ArrayList<ArrayList<String>> getExamsList() {
        return examsList;
    }

    public void setExamsList(ArrayList<ArrayList<String>> examsList) {
        this.examsList = examsList;
    }
}
