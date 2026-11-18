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

public final class StudentTemporaryScoreDataBuilder {

    private StudentTemporaryScoreDataBuilder() {
    }

    public static StudentTemporaryScoreData getStudentTemporaryScoreData(ClientHandler clientHandler) {
        StudentTemporaryScoreData studentTemporaryScoreData = new StudentTemporaryScoreData();
        studentTemporaryScoreData.setResponseType(ResponseType.STUDENT_TEMPORARY_SCORE_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(Score score:clientHandler.getUser().getStudent().getStudentScores()){
            if(score.getScoreType()== ScoreType.TEMPORARY){
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(String.valueOf(score.getScoreId()));
                arrayList.add(DataManager.getCourseById(score.getScoreCourseId()).getCourseName());
                arrayList.add(String.valueOf(score.getScoreValue()));
                arrayList.add(score.getScoreProtestation());
                arrayList.add(score.getScoreProtestationAnswer());
                arrayLists.add(arrayList);
            }
        }
        studentTemporaryScoreData.setStudentTemporaryScores(arrayLists);
        return studentTemporaryScoreData;
    }
}
