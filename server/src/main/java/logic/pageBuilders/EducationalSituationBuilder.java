package logic.pageBuilders;

import java.util.ArrayList;
import logic.enums.ResponseType;
import logic.enums.ScoreType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Score;
import serverDataBase.models.Student;

public final class EducationalSituationBuilder {

    private EducationalSituationBuilder() {
    }

    public static EducationalSituation getEducationalSituationToAssistant(int studentId) {
        EducationalSituation educationalSituation = new EducationalSituation();
        educationalSituation.setResponseType(ResponseType.EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA);

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


                if(score.getScoreValue()>=10.0){
                    passed+=score.getUnits();
                }
                sum+=score.getScoreValue()*score.getUnits();
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


                if(score.getScoreValue()>=10.0){
                    passed+=score.getUnits();
                }
                sum+=score.getScoreValue()*score.getUnits();
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
}
