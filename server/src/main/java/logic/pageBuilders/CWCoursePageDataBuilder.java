package logic.pageBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.EducationalContext;
import serverDataBase.models.Exercise;

public final class CWCoursePageDataBuilder {

    private CWCoursePageDataBuilder() {
    }

    public static CWCoursePageData getCWCourseData(int courseId) {
        CWCoursePageData cwCoursePageData = new CWCoursePageData();
        Course course = DataManager.getCourseById(courseId);

        cwCoursePageData.setExamTime(course.getCourseExamTime().toString());

        ArrayList<ArrayList<String>> arrayLists2 = new ArrayList<>();
        for(Exercise exercise:course.getCourseExercises()){
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(String.valueOf(exercise.getExerciseId()),exercise.getExerciseName(),exercise.getExerciseDeadline().toString()));
            arrayLists2.add(arrayList);

        }
        cwCoursePageData.setExercises(arrayLists2);

        ArrayList<ArrayList<String>> arrayLists3 = new ArrayList<>();
        for(EducationalContext educationalContext:course.getCourseEducationalContexts()){
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(String.valueOf(educationalContext.getEducationalContextId()),educationalContext.getEducationalContextName(),educationalContext.getEducationalContextTime().toString()));
            arrayLists3.add(arrayList);
        }
        cwCoursePageData.setEducationalContexts(arrayLists3);
        return cwCoursePageData;

    }
}
