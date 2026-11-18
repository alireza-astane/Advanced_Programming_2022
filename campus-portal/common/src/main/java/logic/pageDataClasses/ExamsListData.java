package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;


import java.util.ArrayList;

public class ExamsListData extends Response {
    public ExamsListData(){
        super();
        this.setResponseType(ResponseType.EXAMS_TIME);
    }

    ArrayList<ArrayList<String>> examsList;



    //ArrayList:   courseId,courseName,examTime


    public ArrayList<ArrayList<String>> getExamsList() {
        return examsList;
    }

    public void setExamsList(ArrayList<ArrayList<String>> examsList) {
        this.examsList = examsList;
    }
}
