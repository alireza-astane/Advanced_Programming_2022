package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.enums.CourseType;
import logic.enums.MasterGrade;
import logic.enums.MasterType;
import logic.enums.UserType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Requester {
    static Logger log = LogManager.getLogger(Requester.class);
    PrintWriter printWriter;

    GsonBuilder gsonBuilder = Protocol.GSON.newBuilder();

    Gson gson = gsonBuilder.create();

    Client client;
    public Requester(Client client) throws IOException {
        this.client = client;
        if(client.online) {
            printWriter = new PrintWriter(new java.io.OutputStreamWriter(
                    client.socket.getOutputStream(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }
    public void sendRequest(Request request){
        switch (request.getRequestType()){
            case REQUEST_CREATE_CHAT, ADD_STUDENT_TO_CW, ADD_MASTER, ADD_EXERCISE, ADD_COURSE, EDIT_COURSE, EDIT_MASTER, DDD_UNIREQUEST, MINOR_UNIREQUEST, LOGIN, REQUEST_SEND_MESSEGE -> {}
            default -> client.getPinger().setLastPageRequest(request);
        }





        request.setAuthToken(client.authToken);
        log.info("Send Request to server: "+request.getRequestType());
        if(client.online) {
            printWriter.print(gson.toJson(request));
            printWriter.print('\n');
            printWriter.flush();
        }
        else {
            //do offline requests
            log.info("Send Request to OfflineHandler: "+request.getRequestType());
        }
    }

    public void requestPageData(RequestType requestType) {   //for no arg request
        sendRequest(new Request(requestType));
    }

    public void requestEducationalContext(String contextId){
        Request request = new Request(RequestType.CW_EDUCATIONAL_CONTEXT);
        request.setArrayList(new ArrayList<>(Collections.singletonList(contextId)));
        sendRequest(request);
    }
    public void requestMohseniStudentInfo(String studentId){
        Request request = new Request(RequestType.MOHSENI_STUDENT_INFO);
        request.setArrayList(new ArrayList<>(Arrays.asList(studentId)));
        sendRequest(request);
    }

    public void requestStudentExercise(String exerciseId){
        Request request = new Request(RequestType.STUDENT_EXERCISE_DATA);
        request.setArrayList(new ArrayList<>(Collections.singletonList(exerciseId)));
        sendRequest(request);
    }

    public void requestStudentSearch(String departmentId, String entryYear, String studentType){
        Request request = new Request(RequestType.MOHSENI_STUDENT_SEARCH_DATA);
        request.setArrayList(new ArrayList<>(Arrays.asList(departmentId,entryYear,studentType)));
        sendRequest(request);
    }

    public void requestMasterTemporaryScores(String courseId){
        Request request = new Request(RequestType.MASTER_TEMPORARY_SCORE_DATA);
        request.setArrayList(new ArrayList<>(Collections.singletonList(courseId)));
        sendRequest(request);
    }
    public void requestEducationalSituationToAssistant(String studentId){
        Request request = new Request(RequestType.EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA);
        request.setArrayList(new ArrayList<>(Collections.singletonList(studentId)));
        sendRequest(request);
    }

    public void requestTATemporaryScores(String courseId,String masterId,String studentId){
        Request request = new Request(RequestType.ASSISTANT_TEMPORARY_SCORE_DATA);
        request.setArrayList(new ArrayList<>(Arrays.asList(courseId,masterId,studentId)));
        sendRequest(request);
    }
    public void requestMastersListData(String departmentId, String name, String masterGrade){
        Request request = new Request(RequestType.MASTER_LIST_DATA);
        request.setArrayList(new ArrayList<>(Arrays.asList(departmentId,name,masterGrade)));
        sendRequest(request);

    }
    public void requestCoursesListData(String courseNumber,String unit,String departmentId){
        Request request = new Request(RequestType.COURSE_LIST_DATA);
        request.setArrayList(new ArrayList<>(Arrays.asList(courseNumber,unit,departmentId)));
        sendRequest(request);
    }
    public void requestLogin(String name, String pass, String captcha) {
        Request request = new Request(RequestType.LOGIN);
        request.setArrayList(new ArrayList<>(Arrays.asList(name,pass,captcha)));
        sendRequest(request);
    }
    public  void requestLogout(){
        sendRequest(new Request(RequestType.LOGOUT));
    }
    public void requestDeleteMaster(String masterId){
        Request request = new Request(RequestType.DELETE_MASTER);
        request.setArrayList(new ArrayList<>(Collections.singletonList(masterId)));
        sendRequest(request);
    }
    public void requestDeleteCourse(String courseId){
        Request request = new Request(RequestType.DELETE_COURSE);
        request.setArrayList(new ArrayList<>(Collections.singletonList(courseId)));
        sendRequest(request);
    }
    public  void requestRecommendation(String masterId){
        Request request = new Request(RequestType.REQUEST_RECOMMENDATION);
        request.setArrayList(new ArrayList<>(Collections.singletonList(masterId)));
        sendRequest(request);
    }
    public void  requestEnrollmentCertification(){
        sendRequest(new Request(RequestType.ENROLLMENT_REQUEST));
    }
    public void requestMinor(String departmentId){
        Request request = new Request(RequestType.MINOR_UNIREQUEST);
        request.setArrayList(new ArrayList<>(Collections.singletonList(departmentId)));
        sendRequest(request);
    }
    public void  requestWithdraw(){
        sendRequest(new Request(RequestType.WITHDRAW_REQUEST));
    }
    public void  requestDormitory(){
        sendRequest(new Request(RequestType.DORMITPRY_REQUEST));
    }
    public  void requestDoctoralDissertationDefense(){
        sendRequest(new Request(RequestType.DDD_UNIREQUEST));
    }
    public void requestScoreProtestation(String scoreId,String presentation){
        Request request = new Request(RequestType.SCORE_PROTESTATION);
        request.setArrayList(new ArrayList<>(Arrays.asList(scoreId,presentation)));
        sendRequest(request);
    }
    public void requestAnswerScoreProtestation(String scoreId,String protestationAnswer){
        Request request = new Request(RequestType.PROTESTATION_ANSWER);
        request.setArrayList(new ArrayList<>(Arrays.asList(scoreId,protestationAnswer)));
        sendRequest(request);
    }
    public void requestSetScoreValue(String scoreId,String scoreValue){
        Request request = new Request(RequestType.SET_SCORE_VALUE);
        request.setArrayList(new ArrayList<>(Arrays.asList(scoreId,scoreValue)));
        sendRequest(request);
    }
    public void requestVerifyScores(String courseId){
        Request request = new Request(RequestType.VERIFY_SCORES);
        request.setArrayList(new ArrayList<>(Arrays.asList(courseId)));
        sendRequest(request);
    }
    public void requestEditScoreValue(String scoreId,String scoreValue){
        Request request = new Request(RequestType.EDIT_SCORE_VALUE);
        request.setArrayList(new ArrayList<>(Arrays.asList(scoreId,scoreValue)));
        sendRequest(request);
    }
    public void requestChangeEmail(String newEmail){
        Request request = new Request(RequestType.CHANGE_EMAIL);
        request.setArrayList(new ArrayList<>(Collections.singletonList(newEmail)));
        sendRequest(request);
    }
    public void requestChangePhoneNumber(String newPhoneNumber){
        Request request = new Request(RequestType.CHANGE_PHONE_NUMBER);
        request.setArrayList(new ArrayList<>(Collections.singletonList(newPhoneNumber)));
        sendRequest(request);
    }

    public void requestChangePass(String currentPass,String newPass,String confirm){
        Request request = new Request(RequestType.CHANGE_PASSWORD);
        request.setArrayList(new ArrayList<>(Arrays.asList(currentPass,newPass,confirm)));
        sendRequest(request);
    }
    public void requestStarOrDeStarCourse(String courseId){
        Request request = new Request(RequestType.STAR_OR_DESATR_COURSE);
        request.setArrayList(new ArrayList<>(Collections.singletonList(courseId)));
        sendRequest(request);
    }
    public void requestSelectCourse(String courseId){
        Request request = new Request(RequestType.SELECT_COURSE);
        request.setArrayList(new ArrayList<>(Collections.singletonList(courseId)));
        sendRequest(request);
    }

    public void requestAssistantUnitSelectionRequest(String courseId){
        Request request = new Request(RequestType.REQUEST_SELCECT_UNIT_FORM_ASSISTANT);
        request.setArrayList(new ArrayList<>(Collections.singletonList(courseId)));
        sendRequest(request);
    }
    public void requestDeSelectCourse(String courseId){
        Request request = new Request(RequestType.DESELECT_COURSE);
        request.setArrayList(new ArrayList<>(Collections.singletonList(courseId)));
        sendRequest(request);
    }
    public void requestAcceptOrRejectUniRequest(String requestId,boolean answer){
        Request request = new Request(RequestType.ANSWER_UNIREQUEST);
        request.setArrayList(new ArrayList<>(Arrays.asList(requestId,String.valueOf(answer))));
        sendRequest(request);
    }
    public void requestSendMessage(String chatId,String text,byte[] file){
        Request request = new Request(RequestType.REQUEST_SEND_MESSEGE);
        request.setFileInByte(file);
        request.setArrayList(new ArrayList<>(Arrays.asList(chatId,text)));
        sendRequest(request);
    }
    public void requestCreateChat(String shipNumber){
        Request request = new Request(RequestType.REQUEST_CREATE_CHAT);
        request.setArrayList(new ArrayList<>(Collections.singletonList(shipNumber)));
        sendRequest(request);
    }
    public void requestMessageToAdmin(String text){
        Request request = new Request(RequestType.MESSAGE_TO_ADMIN);
        request.setArrayList(new ArrayList<>(Collections.singletonList(text)));
        sendRequest(request);
    }
    public void requestAddTAToCW(String studentShipNumber,String courseId){
        Request request = new Request(RequestType.ADD_TA_TO_CW);
        request.setArrayList(new ArrayList<>(Arrays.asList(studentShipNumber,courseId)));
        sendRequest(request);}
    public void requestAddStudentToCW(String studentShipNumber,String courseId){
        Request request = new Request(RequestType.ADD_STUDENT_TO_CW);
        request.setArrayList(new ArrayList<>(Arrays.asList(studentShipNumber,courseId)));
        sendRequest(request);
    }
    public  void  deleteEducationalContext(String educationalContext){
        Request request = new Request(RequestType.DELETE_EDUCATIONAL_CONTEXT);
        request.setArrayList(new ArrayList<>(Collections.singletonList(educationalContext)));
        sendRequest(request);
    }
    public void requestChangeGroup(String courseId,String newGroup){
        Request request = new Request(RequestType.CHANGE_GOUP);
        request.setArrayList(new ArrayList<>(Arrays.asList(courseId,newGroup)));
        sendRequest(request);
    }
    public void requestScoreAnswers(String answerId,String scoreValue){
        Request request = new Request(RequestType.SCORING_ANSWER);
        request.setArrayList(new ArrayList<>(Arrays.asList(answerId,scoreValue)));
        sendRequest(request);
    }
    public void requestEditCourse(String courseId, String courseName, String courseNumber, String courseDepartmentId,
                                  String courseMasterId, ArrayList<String> courseWeaklyPlan, String courseExamTime, String courseCapacity, String courseType, String courseUnits, String coursePrerequisiteId, String courseCorequisiteId){
       Request request = new Request(RequestType.EDIT_COURSE);
       request.setArrayList(new ArrayList<>(Arrays.asList(courseId,courseName,courseNumber,courseDepartmentId,courseMasterId,courseWeaklyPlan.toString(),courseExamTime,courseCapacity,courseType,courseUnits,coursePrerequisiteId,courseCorequisiteId)));
       sendRequest(request);
    }
    public void requestAddCourse(String courseName, String courseNumber,
                                  String courseMasterId, ArrayList<String> courseWeaklyPlan, String courseExamTime, String courseCapacity, String courseType, String courseUnits, String coursePrerequisiteId, String courseCorequisiteId){
        Request request = new Request(RequestType.ADD_COURSE);
        request.setArrayList(new ArrayList<>(Arrays.asList(courseName,courseNumber,courseMasterId,courseWeaklyPlan.toString(),courseExamTime,courseCapacity,courseType,courseUnits,coursePrerequisiteId,courseCorequisiteId)));
        sendRequest(request);
    }
    public void requestEditMaster(String id,String masterType, String masterGrade, String masteryNumber, String masterRoomNumber, String masterDepartmentId,
                                  String username, String userFullName, String userType, byte[] userImage, String userEmail, String userPhoneNumber, String userNationalNumber){
        Request request = new Request(RequestType.EDIT_MASTER);
        request.setFileInByte(userImage);
        request.setArrayList(new ArrayList<>(Arrays.asList(id,masterType,masterGrade,masteryNumber,masterRoomNumber,masterDepartmentId,username,userFullName,userType,userEmail,userPhoneNumber,userNationalNumber)));
        sendRequest(request);
    }
    public void requestAddMaster(String masterType, String masterGrade, String masteryNumber, String masterRoomNumber,
                                  String username, String userFullName, byte[] userImage, String userEmail, String userPhoneNumber, String userNationalNumber,String password){
        Request request = new Request(RequestType.ADD_MASTER);
        request.setFileInByte(userImage);
        request.setArrayList(new ArrayList<>(Arrays.asList(masterType,masterGrade,masteryNumber,masterRoomNumber,username,userFullName,userEmail,userPhoneNumber,userNationalNumber,password)));
        sendRequest(request);
    }
    public void requestEditEducationalContext(String educationalContextId,String name,String text,byte[] file){
        Request request = new Request(RequestType.EDIT_EDUCATIONAL_CONTEXT);
        request.setFileInByte(file);
        request.setArrayList(new ArrayList<>(Arrays.asList(educationalContextId,name,text)));
        sendRequest(request);
    }
    public void requestAddEducationalContext(String courseId,String name,String text,byte[] file){
        Request request = new Request(RequestType.ADD_EDUCATIONAL_CONTEXT);
        request.setFileInByte(file);
        request.setArrayList(new ArrayList<>(Arrays.asList(courseId,name,text)));
        sendRequest(request);
    }
    public  void requestAddExercise(String courseId,String exerciseName,String openTime,String closeTime,String deadLine,String explanation,String answerType,byte[] file){
        Request request = new Request(RequestType.ADD_EXERCISE);
        request.setFileInByte(file);
        request.setArrayList(new ArrayList<>(Arrays.asList(courseId,exerciseName,openTime,closeTime,deadLine,explanation,answerType)));
        sendRequest(request);
    }
    public void requestUploadAnswer(String exerciseId,String answerText,byte[] answerFile){
        Request request = new Request(RequestType.UPLOAD_ANSWER);
        request.setFileInByte(answerFile);
        request.setArrayList(new ArrayList<>(Arrays.asList(exerciseId,answerText)));
        sendRequest(request);
    }
    public void requestUnitSelectionTimes(ArrayList<String> arrayList){
        Request request = new Request(RequestType.SET_UNIT_SELECTION_TIME);
        request.setArrayList(arrayList);
        sendRequest(request);
    }

}
