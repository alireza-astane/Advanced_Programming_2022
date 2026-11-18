package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.ScoreType;


import network.Response;

import java.util.ArrayList;

public class AssistantTemporaryScoreData extends Response {
    String filterType;
    int courseId;
    ArrayList<ArrayList<String>> masterTemporaryScores;
    ArrayList<ArrayList<String>> studentTemporaryScores;

    double courseAverage;



    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
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

    public ArrayList<ArrayList<String>> getStudentTemporaryScores() {
        return studentTemporaryScores;
    }

    public void setStudentTemporaryScores(ArrayList<ArrayList<String>> studentTemporaryScores) {
        this.studentTemporaryScores = studentTemporaryScores;
    }

    public double getCourseAverage() {
        return courseAverage;
    }

    public void setCourseAverage(double courseAverage) {
        this.courseAverage = courseAverage;
    }

    public int getFailedStudentsNum() {
        return failedStudentsNum;
    }

    public void setFailedStudentsNum(int failedStudentsNum) {
        this.failedStudentsNum = failedStudentsNum;
    }

    public int getPassedStudentsNum() {
        return passedStudentsNum;
    }

    public void setPassedStudentsNum(int passedStudentsNum) {
        this.passedStudentsNum = passedStudentsNum;
    }

    public double getCourseAverageWithoutFailed() {
        return courseAverageWithoutFailed;
    }

    public void setCourseAverageWithoutFailed(double courseAverageWithoutFailed) {
        this.courseAverageWithoutFailed = courseAverageWithoutFailed;
    }

    int failedStudentsNum;
    int passedStudentsNum;
    double courseAverageWithoutFailed;
}
