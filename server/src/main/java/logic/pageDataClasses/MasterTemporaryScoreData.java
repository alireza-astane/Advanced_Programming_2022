package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.Score;

import java.util.ArrayList;


public class MasterTemporaryScoreData extends Response {
    ArrayList<ArrayList<String>> courses;
    //id courseNumber coursename
    int courseId;

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
            try {
                for(Score score: DataManager.getCourseById(courseId).getScores()){
                    ArrayList<String> arrayList2 = new ArrayList<>();
                    arrayList2.add(String.valueOf(score.getScoreId()));
                    arrayList2.add(String.valueOf(score.getScoreStudentId()));
                    arrayList2.add(String.valueOf(score.getScoreValue()));
                    arrayList2.add(score.getScoreProtestation());
                    arrayList2.add(score.getScoreProtestationAnswer());

                    arrayLists2.add(arrayList2);
                }
            } catch (Exception ignored) {
            }
            masterTemporaryScoreData.setMasterTemporaryScores(arrayLists2);
        }

        return masterTemporaryScoreData;



    }

    public ArrayList<ArrayList<String>> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<ArrayList<String>> courses) {
        this.courses = courses;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public ArrayList<ArrayList<String>> getMasterTemporaryScores() {
        return masterTemporaryScores;
    }

    public void setMasterTemporaryScores(ArrayList<ArrayList<String>> masterTemporaryScores) {
        this.masterTemporaryScores = masterTemporaryScores;
    }

    ArrayList<ArrayList<String>> masterTemporaryScores;
    //data: scoreID studentId score eetraz answer
}
