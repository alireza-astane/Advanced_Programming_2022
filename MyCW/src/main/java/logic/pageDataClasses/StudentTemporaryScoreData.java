package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.ScoreType;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Score;

import java.util.ArrayList;

@Getter
@Setter
public class StudentTemporaryScoreData extends Response {
    ArrayList<ArrayList<String>> studentTemporaryScores;

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
    //list: courseId scoreValue eetraz course name


    public ArrayList<ArrayList<String>> getStudentTemporaryScores() {
        return studentTemporaryScores;
    }

    public void setStudentTemporaryScores(ArrayList<ArrayList<String>> studentTemporaryScores) {
        this.studentTemporaryScores = studentTemporaryScores;
    }
}
