package logic.pageDataClasses;
import logic.enums.ResponseType;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Answer;
import serverDataBase.models.Exercise;

import java.util.ArrayList;
import java.util.HashMap;
@Getter
@Setter
public class CWMasterExerciseData extends Response {
    String name;
    byte[] file;
    String explanation;

    public static CWMasterExerciseData getCWMasterExerciseData(int exerciseId) {
        CWMasterExerciseData cwMasterExerciseData = new CWMasterExerciseData();
        cwMasterExerciseData.setResponseType(ResponseType.CW_MASTER_EXERCISE_DATA);

        Exercise exercise = DataManager.getExerciseById(exerciseId);
        cwMasterExerciseData.setExplanation(exercise.getExplanation());
        cwMasterExerciseData.setCloseTime(exercise.getExerciseEndTime().toString());
        cwMasterExerciseData.setDeadLine(exercise.getExerciseDeadline().toString());
        cwMasterExerciseData.setOpenTime(exercise.getExerciseStartTime().toString());
        cwMasterExerciseData.setFile(exercise.getExerciseFile());
        cwMasterExerciseData.setName(exercise.getExerciseName());
        cwMasterExerciseData.setExerciseText(exercise.getExerciseContext());

        HashMap<Integer,ArrayList<Object>> hashMap = new HashMap<>();
        for(Answer answer: exercise.getExerciseAnswers()){
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(DataManager.getUserById(answer.getAnswerStudentID()).getStudent().getStudentshipNumber());
            arrayList.add(answer.getAnswerFile());
            arrayList.add(answer.getAnswerText());
            arrayList.add(answer.getAnswerScore());

            hashMap.put(answer.getAnswerId(),arrayList);
        }
        cwMasterExerciseData.setAnswers(hashMap);
        return cwMasterExerciseData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getExerciseText() {
        return exerciseText;
    }

    public void setExerciseText(String exerciseText) {
        this.exerciseText = exerciseText;
    }

    public HashMap<Integer, ArrayList<Object>> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<Integer, ArrayList<Object>> answers) {
        this.answers = answers;
    }

    String closeTime;
    String deadLine;
    String openTime;
    String exerciseText;

    HashMap<Integer, ArrayList<Object>> answers;   //answer id studentshipNumber (answerFile answerStr Score)

}
