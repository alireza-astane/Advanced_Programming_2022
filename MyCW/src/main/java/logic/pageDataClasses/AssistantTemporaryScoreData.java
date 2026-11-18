package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.ScoreType;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Course;
import serverDataBase.models.Master;
import serverDataBase.models.Score;
import serverDataBase.models.Student;

import java.util.ArrayList;
@Getter
@Setter
public class AssistantTemporaryScoreData extends Response {
    String filterType;
    int courseId;
    ArrayList<ArrayList<String>> masterTemporaryScores;
    ArrayList<ArrayList<String>> studentTemporaryScores;

    double courseAverage;

    public static AssistantTemporaryScoreData getAssistantTemporaryScoreData(ClientHandler clientHandler, String courseId, String masterId, String studentId) {
        AssistantTemporaryScoreData assistantTemporaryScoreData =new AssistantTemporaryScoreData();
        assistantTemporaryScoreData.setResponseType(ResponseType.ASSISTANT_TEMPORARY_SCORE_DATA);

        if(studentId!=null){
            try{
            Student student = DataManager.getUserById(Integer.parseInt(studentId)).getStudent();

            ArrayList<ArrayList<String>> arrayLists1 = new ArrayList<>();
            for(Score score:student.getStudentScores()){
                try {
                    if (score.getScoreType() == ScoreType.TEMPORARY) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(String.valueOf(score.getScoreId()));
                        arrayList.add(DataManager.getCourseById(score.getScoreCourseId()).getCourseName());
                        arrayList.add(String.valueOf(score.getScoreValue()));
                        arrayList.add(score.getScoreProtestation());
                        arrayList.add(score.getScoreProtestationAnswer());
                        arrayLists1.add(arrayList);
                    }
                } catch (Exception ignored) {
                }
            }

            assistantTemporaryScoreData.setStudentTemporaryScores(arrayLists1);} catch (NumberFormatException ignored) {
            }
        }

        else{if(masterId!=null){
            try{
            Master master = DataManager.getUserById(Integer.parseInt(masterId)).getMaster();
            ArrayList<ArrayList<String>> arrayLists2 = new ArrayList<>();
            for(Course course:master.getMasterCourses()){
                try {
                for(Score score:course.getScores()){
                    try {
                    ArrayList<String> arrayList =new ArrayList<>();
                        try {
                            arrayList.add(String.valueOf(score.getScoreId()));
                        } catch (Exception e) {
                        }
                        try {
                            arrayList.add(DataManager.getUserById(score.getScoreStudentId()).getUserFullName());
                        } catch (Exception e) {

                        }
                        try {
                            arrayList.add(DataManager.getUserById(score.getScoreStudentId()).getStudent().getStudentshipNumber());
                        } catch (Exception e) {
                        }
                        try {
                            arrayList.add(String.valueOf(score.getScoreValue()));
                        } catch (Exception e) {

                        }
                        try {
                            arrayList.add(score.getScoreProtestation());
                        } catch (Exception e) {

                        }
                        try {
                            arrayList.add(score.getScoreProtestationAnswer());
                        } catch (Exception e) {

                        }
                        arrayLists2.add(arrayList);} catch (Exception ignored) {
                    }
                }} catch (Exception ignored) {
                }
            }
            assistantTemporaryScoreData.setMasterTemporaryScores(arrayLists2);} catch (NumberFormatException ignored) {
            }
        }
        else if(courseId!=null){
            int scoreCount =0;
            int passedCount = 0;
            int failedCount = 0;
            double sum1 = 0;
            double sum2 = 0;
            try {
            Course course = DataManager.getCourseById(Integer.parseInt(courseId));


                try {
                    for(Score score:course.getScores()){
                        try {
                            try {
                                if(score.getScoreType()==ScoreType.TEMPORARY){
                                    scoreCount++;
                                    sum1 +=score.getScoreValue();
                                    try {
                                        if (score.getScoreValue() >= 10){
                                            passedCount++;
                                            try {
                                                sum2 +=score.getScoreValue();
                                            } catch (Exception e) {
                                            }
                                        }
                                        else
                                            failedCount++;
                                    } catch (Exception e) {
                                    }
                                }
                            } catch (Exception e) {
                            }
                        } catch (Exception ignored) {}
                    }
                } catch (Exception e) {
                }
            } catch (NumberFormatException ignored) {
            }

            assistantTemporaryScoreData.setFailedStudentsNum(failedCount);
            assistantTemporaryScoreData.setPassedStudentsNum(passedCount);

            try{
                assistantTemporaryScoreData.setCourseAverage(sum1/scoreCount);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try{
                assistantTemporaryScoreData.setCourseAverageWithoutFailed(sum2/scoreCount);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }}

        return assistantTemporaryScoreData;


    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
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

    public ArrayList<ArrayList<String>> getStudentTemporaryScores() {
        return studentTemporaryScores;
    }

    public void setStudentTemporaryScores(ArrayList<ArrayList<String>> studentTemporaryScores) {
        this.studentTemporaryScores = studentTemporaryScores;
    }

    public double getCourseAverage() {
        return courseAverage;
    }

    public void setCourseAverage(double courseAverage) {
        this.courseAverage = courseAverage;
    }

    public int getFailedStudentsNum() {
        return failedStudentsNum;
    }

    public void setFailedStudentsNum(int failedStudentsNum) {
        this.failedStudentsNum = failedStudentsNum;
    }

    public int getPassedStudentsNum() {
        return passedStudentsNum;
    }

    public void setPassedStudentsNum(int passedStudentsNum) {
        this.passedStudentsNum = passedStudentsNum;
    }

    public double getCourseAverageWithoutFailed() {
        return courseAverageWithoutFailed;
    }

    public void setCourseAverageWithoutFailed(double courseAverageWithoutFailed) {
        this.courseAverageWithoutFailed = courseAverageWithoutFailed;
    }

    int failedStudentsNum;
    int passedStudentsNum;
    double courseAverageWithoutFailed;
}
