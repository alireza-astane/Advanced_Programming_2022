package logic.pageBuilders;

import java.util.ArrayList;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.Score;

public final class MasterTemporaryScoreDataBuilder {

    private MasterTemporaryScoreDataBuilder() {
    }

    public static MasterTemporaryScoreData getMasterTemporaryScoreData(ClientHandler clientHandler, Integer courseId) {
        MasterTemporaryScoreData masterTemporaryScoreData = new MasterTemporaryScoreData();
        masterTemporaryScoreData.setResponseType(ResponseType.MASTER_TEMPORARY_SCORE_DATA);

        ArrayList<ArrayList<String>>  arrayLists1 = new ArrayList<>();
        for(Course course:clientHandler.getUser().getMaster().getMasterCourses()){
            ArrayList<String> arrayList1 = new ArrayList<>();
            arrayList1.add(String.valueOf(course.getCourseId()));
            arrayList1.add(course.getCourseNumber());
            arrayList1.add(course.getCourseName());
            arrayLists1.add(arrayList1);
        }

        masterTemporaryScoreData.setCourses(arrayLists1);


        if(courseId!=null){
            ArrayList<ArrayList<String>> arrayLists2 = new ArrayList<>();
            for(Score score: DataManager.getCourseById(courseId).getScores()){
                ArrayList<String> arrayList2 = new ArrayList<>();
                arrayList2.add(String.valueOf(score.getScoreId()));
                arrayList2.add(String.valueOf(score.getScoreStudentId()));
                arrayList2.add(String.valueOf(score.getScoreValue()));
                arrayList2.add(score.getScoreProtestation());
                arrayList2.add(score.getScoreProtestationAnswer());

                arrayLists2.add(arrayList2);
            }
        
            masterTemporaryScoreData.setMasterTemporaryScores(arrayLists2);
        }

        return masterTemporaryScoreData;



    }
}
