package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;
import java.util.ArrayList;


public class MasterTemporaryScoreData extends Response {
    ArrayList<ArrayList<String>> courses;
    //id courseNumber coursename
    int courseId;


    public ArrayList<ArrayList<String>> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<ArrayList<String>> courses) {
        this.courses = courses;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public ArrayList<ArrayList<String>> getMasterTemporaryScores() {
        return masterTemporaryScores;
    }

    public void setMasterTemporaryScores(ArrayList<ArrayList<String>> masterTemporaryScores) {
        this.masterTemporaryScores = masterTemporaryScores;
    }

    ArrayList<ArrayList<String>> masterTemporaryScores;
    //data: scoreID studentId score eetraz answer
}
