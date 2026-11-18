package logic.pageBuilders;

import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Answer;
import serverDataBase.models.Exercise;

public final class CWStudentExerciseDataBuilder {

    private CWStudentExerciseDataBuilder() {
    }

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
        cwStudentExerciseData.setCloseTime(exercise.getExerciseEndTime().toString());
        cwStudentExerciseData.setExplanation(exercise.getExplanation());
        cwStudentExerciseData.setDeadLine(exercise.getExerciseDeadline().toString());
        cwStudentExerciseData.setName(exercise.getExerciseName());
        cwStudentExerciseData.setFile(exercise.getExerciseFile());
        cwStudentExerciseData.setOpenTime(exercise.getExerciseStartTime().toString());
        if(answer!=null){
            cwStudentExerciseData.setAnswer(answer.getAnswerText());
            cwStudentExerciseData.setAnswerFile(answer.getAnswerFile());
            cwStudentExerciseData.setScore(String.valueOf(answer.getAnswerScore()));
            cwStudentExerciseData.setUploadTime(answer.getAnswerUploadTime().toString());
        }



        return cwStudentExerciseData;
    }
}
