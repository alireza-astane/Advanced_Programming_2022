package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.ScoreType;


import network.Response;


import java.util.ArrayList;

public class EducationalSituation extends Response {
    int passedUnits;
    double totalAverageScore;







    public int getPassedUnits() {
        return passedUnits;
    }

    public void setPassedUnits(int passedUnits) {
        this.passedUnits = passedUnits;
    }

    public double getTotalAverageScore() {
        return totalAverageScore;
    }

    public void setTotalAverageScore(double totalAverageScore) {
        this.totalAverageScore = totalAverageScore;
    }

    public ArrayList<ArrayList<String>> getList() {
        return list;
    }

    public void setList(ArrayList<ArrayList<String>> list) {
        this.list = list;
    }

    ArrayList<ArrayList<String>> list;
    //list: courseName and score(N/A for not assigned)
}
