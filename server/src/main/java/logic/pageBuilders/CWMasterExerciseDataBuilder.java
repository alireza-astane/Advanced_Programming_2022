package logic.pageBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Answer;
import serverDataBase.models.Exercise;

public final class CWMasterExerciseDataBuilder {

    private CWMasterExerciseDataBuilder() {
    }

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
}
