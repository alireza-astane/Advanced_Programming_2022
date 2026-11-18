package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.ScoreType;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Score;
import serverDataBase.models.Student;

import java.util.ArrayList;
@Getter
@Setter
public class EducationalSituation extends Response {
    int passedUnits;
    double totalAverageScore;

    public static EducationalSituation getEducationalSituationToAssistant(int studentId) {
        EducationalSituation educationalSituation = new EducationalSituation();
        educationalSituation.setResponseType(ResponseType.EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA);

        try {

            Student student = DataManager.getUserById(studentId).getStudent();
            int passed=0;
            int units=0;
            double sum=0;

            ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

            for(Score score:student.getStudentScores()){
                if(score.getScoreType()== ScoreType.VERIFIED){
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add(DataManager.getCourseById(score.getScoreCourseId()).getCourseName());
                    arrayList.add(String.valueOf(score.getScoreValue()));
                    arrayLists.add(arrayList);


                    if(score.getScoreValue()>10.0){
                        passed++;
                    }
                    sum+=score.getScoreValue();
                    units+= score.getUnits();
                }
            }


            if(units!=0){
                educationalSituation.setTotalAverageScore(sum/units);
            }
            educationalSituation.setPassedUnits(passed);
            educationalSituation.setList(arrayLists);
        } catch (Exception ignored) {
        }

        return educationalSituation;
    }

    public static EducationalSituation getEducationalSituationToStudent(ClientHandler clientHandler) {
        EducationalSituation educationalSituation = new EducationalSituation();
        educationalSituation.setResponseType(ResponseType.EDUCATIONAL_SITUATION_TO_STUDENT_DATA);

        int passed=0;
        int units=0;
        double sum=0;

        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        for(Score score:clientHandler.getUser().getStudent().getStudentScores()){
            if(score.getScoreType()==ScoreType.VERIFIED){
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(DataManager.getCourseById(score.getScoreCourseId()).getCourseName());
                arrayList.add(String.valueOf(score.getScoreValue()));
                arrayLists.add(arrayList);


                if(score.getScoreValue()>10.0){
                    passed++;
                }
                sum+=score.getScoreValue();
                units+= score.getUnits();
            }
        }


        if(units!=0){
            educationalSituation.setTotalAverageScore(sum/units);
        }
        educationalSituation.setPassedUnits(passed);
        educationalSituation.setList(arrayLists);

        return educationalSituation;
    }


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
