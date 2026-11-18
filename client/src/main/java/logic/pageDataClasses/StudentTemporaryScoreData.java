package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.ScoreType;


import network.Response;


import java.util.ArrayList;

public class StudentTemporaryScoreData extends Response {
    ArrayList<ArrayList<String>> studentTemporaryScores;


    //list: courseId scoreValue eetraz course name


    public ArrayList<ArrayList<String>> getStudentTemporaryScores() {
        return studentTemporaryScores;
    }

    public void setStudentTemporaryScores(ArrayList<ArrayList<String>> studentTemporaryScores) {
        this.studentTemporaryScores = studentTemporaryScores;
    }
}
