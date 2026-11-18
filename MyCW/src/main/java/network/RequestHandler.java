package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.pageDataClasses.ChangePassData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.DataManager;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static serverDataBase.DataManager.addDormitoryRequest;


public class RequestHandler extends Thread  {

    static Logger log = LogManager.getLogger(RequestHandler.class);
    Socket socket;
    int authToken;
    Scanner scanner;
    ClientHandler clientHandler;
    public RequestHandler(ClientHandler clientHandler) throws IOException {
        this.clientHandler=clientHandler;
        this.socket=clientHandler.socket;
        this.authToken= clientHandler.authToken;
        scanner=new Scanner(socket.getInputStream());
    }
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    public boolean checkAuthToken(Request request){
        return Objects.equals(request.getAuthToken(), String.valueOf(this.authToken));
    }
    public Request decode(String messageStr){
        return gson.fromJson(messageStr, Request.class);
    }

    @Override
    public void run() {
        while ((scanner.hasNext())){
            String input = scanner.nextLine();

            Request request = decode(input);
            log.info("Got request "+request.getRequestType()+" from client "+clientHandler.getId());
            if(checkAuthToken(request)){
                handle(request);
            }
        }
        log.info("client "+clientHandler.getId()+" disconnected");
    }



    public void handle(Request request){
        switch (request.requestType){
            case LOGIN_DATA -> {
                try {
                    clientHandler.responser.sendLoginData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case PING -> {
                clientHandler.responser.sendOfflineData();
            }
            case LOGIN -> {
                try {
                    clientHandler.responser.sendLoginResult(request.getArrayList());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case ADMIN_MENU_DATA -> {
                clientHandler.responser.sendAdminMenuData();
            }
            case MOHSENI_MENU_DATA -> {
                clientHandler.responser.sendMohseniMenuData();
            }
            case WEEKLY_PLAN -> {
                clientHandler.responser.sendWeeklyPlanData();
            }
            case EXAMS_TIME -> {
                clientHandler.responser.sendExamsListData();
            }
            case MESSENGER_MENU -> {
                clientHandler.responser.sendMessengerMenuData();
            }
            case CHAT_DATA -> {
                clientHandler.responser.sendChatData(Integer.parseInt(request.getArrayList().get(0)));
            }
            case CW_MENU_DATA -> {
                clientHandler.responser.sendCWMenuData();
            }
            case STUDENT_PROFILE_DATA -> {
                clientHandler.responser.sendStudentProfileData();
            }
            case GENERAL_PROFILE_DATA -> {
                clientHandler.responser.sendGeneralProfileData();
            }
            case CW_COURSE_PAGE_DATA -> {
                clientHandler.responser.sendCWCourseData(Integer.parseInt(request.getArrayList().get(0)));
            }
            case STUDENT_MAIN_MENU -> {
                clientHandler.responser.sendStudentMenuData();
            }
            case MASTER_MAIN_MENU -> {
                clientHandler.responser.sendMasterMenuData();
            }
            case ASSISTANT_MAIN_MENU -> {
                clientHandler.responser.sendAssistantMenuData();
            }
            case MASTER_PROFILE_DATA -> {
                clientHandler.responser.sendMasterProfileData();
            }
            case MOHSENI_STUDENT_INFO -> {
                clientHandler.responser.sendMohseniStudentInfoData(request.getArrayList().get(0));
            }
            case ADD_CHAT_DATA -> {
                clientHandler.responser.sendAddChatData();
            }
            case UNIREQUESTS_DATA -> {
                clientHandler.responser.sendUniRequestsData();
            }
            case STUDENT_TEMPORARY_SCORE_DATA -> {
                clientHandler.responser.sendStudentTemporaryScores();
            }
            case STUDENT_GENERAL_CALENDER_DATA -> {
                clientHandler.responser.sendStudentGeneralCalenderData();
            }
            case MASTER_GENERAL_CALENDER_DATA -> {
                clientHandler.responser.sendMasterGeneralCalenderData();
            }
            case EDIT_MASTER_DATA -> {
                clientHandler.responser.sendEditMasterData(Integer.parseInt(request.getArrayList().get(0)));
            }
            case STUDENT_EXERCISE_DATA -> {
                clientHandler.responser.sendCWStudentExerciseData(Integer.parseInt(request.getArrayList().get(0)));
            }
            case EDIT_COURSE_DATA -> {
                clientHandler.responser.sendEditCourseData(Integer.parseInt(request.getArrayList().get(0)));
            }
            case COURSE_LIST_DATA -> {
                clientHandler.responser.sendCoursesListData(request.getArrayList().get(0),request.getArrayList().get(1),request.getArrayList().get(2));
            }
            case ASSISTANT_TEMPORARY_SCORE_DATA -> {
                clientHandler.responser.sendTATemporaryScores(request.getArrayList().get(0),request.getArrayList().get(0),request.getArrayList().get(0));
            }
            case CW_MASTER_EXERCISE_DATA -> {
                clientHandler.responser.sendCWMasterExerciseData(Integer.parseInt(request.getArrayList().get(0)));
            }
            case UNIT_SELECTION_DATA -> {
                clientHandler.responser.sendUnitSelectionData();
            }
            case EDUCATIONAL_SITUATION_TO_ASSISTANT_DATA -> {
                clientHandler.responser.sendEducationalSituationToAssistant(Integer.parseInt(request.getArrayList().get(0)));
            }
            case EDUCATIONAL_SITUATION_TO_STUDENT_DATA -> {
                clientHandler.responser.sendEducationalSituationToStudent();
            }
            case MASTER_TEMPORARY_SCORE_DATA -> {
                clientHandler.responser.sendMasterTemporaryScores(Integer.valueOf(request.getArrayList().get(0)));
            }
            case MOHSENI_STUDENT_SEARCH_DATA -> {
                clientHandler.responser.sendMohseniStudentSearchData(request.getArrayList().get(0),request.getArrayList().get(1),request.getArrayList().get(2));
            }
            case MASTER_LIST_DATA -> {
                clientHandler.responser.sendMastersListData(request.getArrayList().get(0),request.getArrayList().get(1),request.getArrayList().get(2));
            }
            case CHANGE_PASS_DATA -> {
                clientHandler.responser.sendChangePassData();
            }
            case CHOOOSE_UNIT_SELECTION_DATA -> {
                clientHandler.responser.sendChooseUnitSelectionTime();
            }
            case DELETE_MASTER -> {
                DataManager.deleteMaster(request.getArrayList().get(0));
            }
            case DELETE_COURSE -> {
                DataManager.deleteCourse(request.getArrayList().get(0));
            }
            case REQUEST_RECOMMENDATION -> {
                DataManager.addRecommendationRequest(clientHandler.getUser().getId(),request.getArrayList());
            }
            case ENROLLMENT_REQUEST -> {
                DataManager.addEnrollmentRequest(clientHandler.getUser().getId());
            }
            case MINOR_UNIREQUEST -> {
                DataManager.addMinorRequest(clientHandler.getUser().getId(),request.getArrayList());
            }
            case WITHDRAW_REQUEST -> {
                DataManager.addWithdrawRequest(clientHandler.getUser().getId());
            }
            case DORMITPRY_REQUEST -> {
                addDormitoryRequest(clientHandler.getUser().getId());
            }
            case DDD_UNIREQUEST -> {
                DataManager.addDDDRequest(clientHandler.getUser().getId());
            }
            case SCORE_PROTESTATION -> {
                DataManager.addScorePortestation(request.getArrayList());
            }
            case PROTESTATION_ANSWER -> {
                DataManager.addScorePortestationAnswer(request.getArrayList());
            }
            case SET_SCORE_VALUE -> {
                DataManager.setScoreValue(request.getArrayList());
            }
            case VERIFY_SCORES -> {
                DataManager.verifyScores(request.getArrayList());
            }
            case EDIT_SCORE_VALUE -> {
                //
            }
            case CHANGE_EMAIL -> {
                DataManager.changeEmail(clientHandler.getUser().getId(),request.getArrayList().get(0));
            }
            case CHANGE_PHONE_NUMBER -> {
                DataManager.changePhoneNumber(clientHandler.getUser().getId(),request.getArrayList().get(0));
            }
            case CHANGE_PASSWORD -> {
                if(!Objects.equals(request.getArrayList().get(0), clientHandler.getUser().getUserPassword())) {
                    ChangePassData changePassData = new ChangePassData();
                    changePassData.setResult("WRONG_PASS");
                    clientHandler.responser.sendResponse(changePassData);

                }
                else if (!Objects.equals(request.getArrayList().get(1), request.getArrayList().get(2))) {
                    ChangePassData changePassData = new ChangePassData();
                    changePassData.setResult("NOT_SAME");
                    clientHandler.responser.sendResponse(changePassData);
                }
                else {
                    DataManager.changePass(clientHandler.getUser().getId(),request.getArrayList().get(1));
                    ChangePassData changePassData = new ChangePassData();
                    changePassData.setResult("CHANGED");
                    clientHandler.responser.sendResponse(changePassData);
                }

            }
            case REQUEST_SELCECT_UNIT_FORM_ASSISTANT -> {
                DataManager.requestUnitSelectionFromAssistant(clientHandler.getUser().getId(),request.getArrayList().get(0));
            }
            case REQUEST_SEND_MESSEGE -> {
                DataManager.addMessage(clientHandler.getUser().getId(),request.getArrayList(),request.getFileInByte());
            }
            case REQUEST_CREATE_CHAT -> {
                DataManager.addCreateChatRequest(clientHandler.getUser().getId(),request.getArrayList().get(0));
            }
            case MESSAGE_TO_ADMIN -> {
                //DataManager.addMessegeToAdmin(clientHandler.getUser().getId(),request.getArrayList().get(0));
            }
            case ADD_TA_TO_CW -> {
                DataManager.addTAToCourse(request.getArrayList().get(0),request.getArrayList().get(1));
            }
            case ADD_STUDENT_TO_CW -> {
                DataManager.addStudentToCourse(request.getArrayList().get(0),request.getArrayList().get(1));
            }
            case DELETE_EDUCATIONAL_CONTEXT -> {
                DataManager.deleteEducationalContext(request.getArrayList().get(0));
            }
            case CHANGE_GOUP -> {
                DataManager.changeGroup(String.valueOf(clientHandler.getUser().getId()),request.getArrayList());
            }
            case SCORING_ANSWER -> {
                DataManager.ScoreAnswer(request.getArrayList().get(0),request.getArrayList().get(1));
            }
            case EDIT_COURSE, ADD_COURSE -> {
                DataManager.AddOrEditCourse(String.valueOf(clientHandler.getUser().getMaster().getMasterDepartmentId()),request.getArrayList());
            }
            case EDIT_MASTER, ADD_MASTER -> {
                DataManager.addOrEditMaster(String.valueOf(clientHandler.getUser().getMaster().getMasterDepartmentId()),request.getArrayList(),request.getFileInByte());

            }
            case ADD_EXERCISE -> {
                DataManager.addExercise(request.getArrayList(),request.getFileInByte());
            }
            case UPLOAD_ANSWER -> {
                DataManager.addAnswer(request.getArrayList(),request.getFileInByte());
            }
            case ANSWER_UNIREQUEST -> {
                DataManager.answerUniRequest(request.getArrayList());
            }
            case EDIT_EDUCATIONAL_CONTEXT, ADD_EDUCATIONAL_CONTEXT -> {
                DataManager.addOrEditEducationContext(request.getArrayList(),request.getFileInByte());
            }
            case SET_UNIT_SELECTION_TIME -> {
                DataManager.setUnitSelectionTime(request.getArrayList());
            }
            case STAR_OR_DESATR_COURSE -> {
                DataManager.statOrDestarCourse(clientHandler.getUser().getStudent(),request.getArrayList().get(0));
            }
            case SELECT_COURSE -> {
                DataManager.preRegisterCourse(clientHandler.getUser().getStudent(),request.getArrayList().get(0));
            }
            case DESELECT_COURSE -> {
                DataManager.undoPreRegisterCourse(clientHandler.getUser().getStudent(),request.getArrayList().get(0));
            }
            case CW_EDUCATIONAL_CONTEXT -> {
            }
        }
    }
}

