package logic.pageDataClasses;

import java.util.ArrayList;

public class CWGeneralCalender {

    public ArrayList<ArrayList<String>> getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(ArrayList<ArrayList<String>> deadlines) {
        this.deadlines = deadlines;
    }

    public ArrayList<ArrayList<String>> getFinalExams() {
        return finalExams;
    }

    public void setFinalExams(ArrayList<ArrayList<String>> finalExams) {
        this.finalExams = finalExams;
    }

    ArrayList<ArrayList<String>> deadlines;  // course name time;

    ArrayList<ArrayList<String>> finalExams; //name time

}
