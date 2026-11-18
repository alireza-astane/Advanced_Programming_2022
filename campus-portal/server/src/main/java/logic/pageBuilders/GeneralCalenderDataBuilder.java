package logic.pageBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.models.Course;
import serverDataBase.models.Exercise;

public final class GeneralCalenderDataBuilder {

    private GeneralCalenderDataBuilder() {
    }

    public static GeneralCalenderData getStudentGeneralCalenderData(ClientHandler clientHandler) {
        GeneralCalenderData generalCalenderData = new GeneralCalenderData();
        generalCalenderData.setResponseType(ResponseType.STUDENT_GENERAL_CALENDER_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        for(Course course:clientHandler.getUser().getStudent().getStudentCourses()){
            arrayLists.add(new ArrayList<>(Arrays.asList(course.getCourseName(),course.getCourseExamTime().toString())));
            for(Exercise exercise:course.getCourseExercises()){
                arrayLists.add(new ArrayList<>(Arrays.asList(exercise.getExerciseName(),exercise.getExerciseDeadline().toString())));
            }
        }

        generalCalenderData.setArrayLists(arrayLists);
        return generalCalenderData;
    }

    public static GeneralCalenderData getMasterGeneralCalenderData(ClientHandler clientHandler) {
        GeneralCalenderData generalCalenderData = new GeneralCalenderData();
        generalCalenderData.setResponseType(ResponseType.MASTER_GENERAL_CALENDER_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        for(Course course:clientHandler.getUser().getMaster().getMasterCourses()){
            arrayLists.add(new ArrayList<>(Arrays.asList(course.getCourseName(),course.getCourseExamTime().toString())));
            for(Exercise exercise:course.getCourseExercises()){
                arrayLists.add(new ArrayList<>(Arrays.asList(exercise.getExerciseName(),exercise.getExerciseDeadline().toString())));
            }
        }
        generalCalenderData.setArrayLists(arrayLists);
        return generalCalenderData;
    }
}
