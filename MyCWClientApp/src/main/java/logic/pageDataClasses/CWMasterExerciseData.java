package logic.pageDataClasses;
import logic.enums.ResponseType;

import network.Response;


import java.util.ArrayList;
import java.util.HashMap;

public class CWMasterExerciseData extends Response {
    String name;
    byte[] file;
    String explanation;


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
