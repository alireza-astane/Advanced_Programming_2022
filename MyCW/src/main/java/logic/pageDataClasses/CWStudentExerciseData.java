package logic.pageDataClasses;

import logic.enums.ResponseType;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Answer;
import serverDataBase.models.Exercise;

@Getter
@Setter
public class CWStudentExerciseData extends Response {
    String name;
    byte[] file;
    String explanation;
    String closeTime;

    public static CWStudentExerciseData getCWStudentExerciseData(ClientHandler clientHandler, int exerciseId) {

        CWStudentExerciseData cwStudentExerciseData = new CWStudentExerciseData();
        cwStudentExerciseData.setResponseType(ResponseType.STUDENT_EXERCISE_DATA);
        Exercise exercise = DataManager.getExerciseById(exerciseId);
        Answer answer = null;
        for(Answer answer1:exercise.getExerciseAnswers()){
            if(answer1.getAnswerStudentID()==clientHandler.getUser().getId()){
                answer = answer1;
            }
        }
        cwStudentExerciseData.setAnswer(answer.getAnswerText());
        cwStudentExerciseData.setCloseTime(exercise.getExerciseEndTime().toString());
        cwStudentExerciseData.setAnswerFile(answer.getAnswerFile());
        cwStudentExerciseData.setExplanation(exercise.getExplanation());
        cwStudentExerciseData.setDeadLine(exercise.getExerciseDeadline().toString());
        cwStudentExerciseData.setName(exercise.getExerciseName());
        cwStudentExerciseData.setFile(exercise.getExerciseFile());
        cwStudentExerciseData.setScore(String.valueOf(answer.getAnswerScore()));
        cwStudentExerciseData.setOpenTime(exercise.getExerciseStartTime().toString());
        cwStudentExerciseData.setUploadTime(answer.getAnswerUploadTime().toString());



        return cwStudentExerciseData;
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

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getAnswerFile() {
        return answerFile;
    }

    public void setAnswerFile(byte[] answerFile) {
        this.answerFile = answerFile;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    String deadLine;
    String uploadTime;
    String openTime;
    String text;

    byte[] answerFile;
    String answer;

    String score;
}
