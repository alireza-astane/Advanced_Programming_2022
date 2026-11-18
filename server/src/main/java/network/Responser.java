package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.Configuration;
import logic.enums.*;
import logic.pageDataClasses.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.DataManager;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Responser {

    static Logger log = LogManager.getLogger(Responser.class);
    PrintWriter printWriter;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    Socket socket;
    int authToken;
    ClientHandler clientHandler;


    public Responser(ClientHandler clientHandler) throws IOException {
        this.clientHandler=clientHandler;
        this.socket=clientHandler.socket;
        printWriter = new PrintWriter(socket.getOutputStream());
        this.authToken= clientHandler.authToken;
    }
    public void sendResponse(Response response){
        log.info("Response "+response.getResponseType()+" sent to client "+clientHandler.getId());
        printWriter.println(gson.toJson(response));
        printWriter.flush();
    }

    public byte[] file2Bytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public File bytes2File(byte[] bytes){
        return null;
    }


    public void sendAuthToken(int authToken){
        AuthToken authToken1 = new AuthToken(authToken);
        sendResponse(authToken1);
    }


    public void sendLoginData() throws IOException {
        LoginData loginData = LoginData.getLoginData(clientHandler);
        sendResponse(loginData);
    }

    public void sendLoginResult(ArrayList<String> arrayList) throws IOException {
        LoginData loginData = new LoginData();

        String username = arrayList.get(0);
        String pass = arrayList.get(1);
        String captcha = arrayList.get(2);
        if(!Server.getInstance().usernameAndPassMap.containsKey(username))
            loginData.setResult("No such username");
        else{
            if(Server.getInstance().usernameAndPassMap.get(username)==pass)
            {loginData.setResult("Wrong password");
                System.out.println(Server.getInstance().usernameAndPassMap.get(username)+" "+ pass);}
            else{
                if(!(Objects.equals(captcha, clientHandler.captchaCode)))
                    loginData.setResult("Wrong captcha code");
                else {
                    clientHandler.setUser(DataManager.getUserByUserName(username));
                    if(!DataManager.getUserByUserName(username).isCanLogin())
                        loginData.setResult("You cant login");
                    else{
                        Duration duration = Duration.between(clientHandler.getUser().getUserLastLoginTime(),LocalDateTime.now());
                        System.out.println(duration.toHours());
                        if(duration.toHours()> Integer.parseInt((String) Configuration.logics.get("loginTimeDifference"))){
                            loginData.setResult("ChangePass");
                        }
                        else {
                            switch (clientHandler.getUser().getUserType()){
                                case ASSISTANT -> {
                                    loginData.setResult("AssistantMenu");
                                    DataManager.setLastloginTime(clientHandler);
                                }
                                case MASTER -> {
                                    loginData.setResult("MasterMenu");
                                    DataManager.setLastloginTime(clientHandler);
                                }
                                case STUDENT -> {
                                    loginData.setResult("StudentMenu");
                                    DataManager.setLastloginTime(clientHandler);
                                }
                                case ADMIN -> {
                                    loginData.setResult("AdminMenu");
                                    DataManager.setLastloginTime(clientHandler);
                                }
                                case MOHSENI -> {
                                    loginData.setResult("MohseniMenu");
                                    DataManager.setLastloginTime(clientHandler);
                                }
                                default -> loginData.setResult("ChangePass");
                            }
                        }
                    }

                }
            }

        }
        List<CaptchaImages> ImageList = Arrays.asList(CaptchaImages.A,CaptchaImages.B,CaptchaImages.C,CaptchaImages.D,CaptchaImages.E);
        Random rand = new Random();
        CaptchaImages RImage = ImageList.get(rand.nextInt(ImageList.size()));
        clientHandler.captchaCode = RImage.code;
        loginData.setCaptchaImageFile(file2Bytes(RImage.path));
        sendResponse(loginData);
    }


    public void sendAdminMenuData(){
        Response response = new Response();
        response.setResponseType(ResponseType.ADMIN_MENU_DATA);
        sendResponse(response);
    }

    public void sendMohseniMenuData(){
        Response response = new Response();
        response.setResponseType(ResponseType.MOHSENI_MENU_DATA);
        sendResponse(response);
    }

    public  void sendStudentMenuData(){
        MainMenuData mainMenuData = MainMenuData.getStudentMainMenuData(clientHandler);
        sendResponse(mainMenuData);
    }

    public void sendMasterMenuData(){
        MainMenuData mainMenuData = MainMenuData.getMasterMainMenuData(clientHandler);
        sendResponse(mainMenuData);
    }

    public void sendAssistantMenuData(){
        MainMenuData mainMenuData = MainMenuData.getAssistantMainMenuData(clientHandler);
        sendResponse(mainMenuData);
    }

    public void sendRegistrationData(){
        Response response = new Response();
        response.setResponseType(ResponseType.REGISTRATION_DATA);
        sendResponse(response);
    }

    public void sendEducationData(){
        Response response = new Response();
        response.setResponseType(ResponseType.EDUCATION_DATA);
        sendResponse(response);
    }

    public void sendWeeklyPlanData(){
        WeeklyPlanData weeklyPlanData = WeeklyPlanData.getWeeklyPlanData(clientHandler);
        sendResponse(weeklyPlanData);
    }

    public void sendExamsListData(){
        ExamsListData examsListData = ExamsListData.getExamsListData(clientHandler);

        sendResponse(examsListData);
    }

    public void sendMinorData(){
        Response response = new Response();
        response.setResponseType(ResponseType.MINOR_UNIREQUEST_DATA);
        sendResponse(response);
    }

    public void sendDoctoralDissertationData(){
        Response response = new Response();
        response.setResponseType(ResponseType.DDD_UNIREQUEST_DATA);
        sendResponse(response);
    }

    public void sendDormitoryData(){
        Response response = new Response();
        response.setResponseType(ResponseType.DORMITORY_UNIREQUEST_DATA);
        sendResponse(response);
    }

    public void sendWithdrawData(){
        Response response = new Response();
        response.setResponseType(ResponseType.WITHDRAW);
        sendResponse(response);
    }

    public void sendRecommendationData(){
        Response response = new Response();
        response.setResponseType(ResponseType.RECOMMENDATION_UNIREQUEST_DATA);
        sendResponse(response);
    }

    public void sendEnrollmentCertificateData(){
        Response response = new Response();
        response.setResponseType(ResponseType.ENROLLMENT_UNIREQUEST_DATA);
        sendResponse(response);
    }

    public void sendAddCourseData(){
        Response response = new Response();
        response.setResponseType(ResponseType.ADD_COURSE_DATA);
        sendResponse(response);
    }
    public void sendAddMasterData(){
        Response response = new Response();
        response.setResponseType(ResponseType.ADD_MASTER_DATA);
        sendResponse(response);
    }
    public void sendChooseUnitSelectionTime(){
        Response response = new Response();
        response.setResponseType(ResponseType.CHOOOSE_UNIT_SELECTION_DATA);
        sendResponse(response);
    }

    public void sendUnitSelectionData(){
        Response response = new Response();
        response.setResponseType(ResponseType.UNIT_SELECTION_DATA);
        sendResponse(response);
    }

    public void sendChatData(int chatId){
        ChatData chatData = ChatData.getChatData(clientHandler,chatId);
        sendResponse(chatData);
    }

    public void sendMessengerMenuData(){
        MessengerData messengerData = MessengerData.getMessengerMenuData(clientHandler);
        sendResponse(messengerData);
    }

    public void sendCWMenuData(){
        CWMenuData cwMenuData = CWMenuData.getCwMenuData(clientHandler);
        sendResponse(cwMenuData);
    }

    public void sendCWCourseData(int courseId){
        CWCoursePageData cwCoursePageData = CWCoursePageData.getCWCourseData(courseId);
        sendResponse(cwCoursePageData);
    }

    public void sendMasterProfileData(){
        ProfileData profileData = ProfileData.getMasterProfileData(clientHandler);
        sendResponse(profileData);
    }

    public void sendGeneralProfileData(){
        ProfileData profileData = ProfileData.getGeneralProfileData(clientHandler);
        sendResponse(profileData);
    }

    public void sendStudentProfileData(){
        ProfileData profileData = ProfileData.getStudentProfileData(clientHandler);
        sendResponse(profileData);
    }

    public void sendChangePassData(){
        Response response = new Response();
        response.setResponseType(ResponseType.CHANGE_PASS_DATA);
        sendResponse(response);
    }

    public void senCWEducationalContextData(int cwEducationalContextId){
        CWEducationalContextData cwEducationalContextData = CWEducationalContextData.getCWEducationalContextData(cwEducationalContextId);
        sendResponse(cwEducationalContextData);
    }

    public void sendAddChatData(){
        AddChatData addChatData = AddChatData.getAddChatData(clientHandler);
        sendResponse(addChatData);
    }

    public void sendMohseniStudentInfoData(String studentUserId){
        MohseniStudentInfoData mohseniStudentInfoData = MohseniStudentInfoData.getMohseniStudentInfoData(Integer.parseInt(studentUserId));
        sendResponse(mohseniStudentInfoData);
    }

    public void sendUniRequestsData(){
        UniRequestsData uniRequestsData = UniRequestsData.getUniRequestData(clientHandler);
        sendResponse(uniRequestsData);
    }

    public void sendStudentTemporaryScores(){
        StudentTemporaryScoreData studentTemporaryScoreData= StudentTemporaryScoreData.getStudentTemporaryScoreData(clientHandler);
        sendResponse(studentTemporaryScoreData);
    }



    public void sendStudentGeneralCalenderData(){
        GeneralCalenderData generalCalenderData = GeneralCalenderData.getStudentGeneralCalenderData(clientHandler);
        sendResponse(generalCalenderData);
    }

    public void sendMasterGeneralCalenderData(){
        GeneralCalenderData generalCalenderData = GeneralCalenderData.getMasterGeneralCalenderData(clientHandler);
        sendResponse(generalCalenderData);
    }



    public void sendEditMasterData(int masterId){
        EditMasterData editMasterData =EditMasterData.getEditMasterData(masterId);
        sendResponse(editMasterData);
    }

    public void sendCWStudentExerciseData(int exerciseId){
        CWStudentExerciseData cwStudentExerciseData =CWStudentExerciseData.getCWStudentExerciseData(clientHandler,exerciseId);
        sendResponse(cwStudentExerciseData);
    }

    public void sendEditCourseData(int courseId){
        EditCourseData editCourseData = EditCourseData.getEditCourseData(courseId);
        sendResponse(editCourseData);

    }
    public void sendCWMasterExerciseData(int exerciseId){
        CWMasterExerciseData cwMasterExerciseData =CWMasterExerciseData.getCWMasterExerciseData(exerciseId);
        sendResponse(cwMasterExerciseData);
    }

    public void sendMohseniMessengerData(){
        MohseniMessengerData mohseniMessengerData = new MohseniMessengerData();
        sendResponse(mohseniMessengerData);
    }



    public void sendMohseniStudentSearchData(String departmentId, String entryYear, String studentType){
        MohseniStudentSearchData mohseniStudentSearchData = MohseniStudentSearchData.getMohseniStudentSearchData(departmentId,entryYear,studentType);
        sendResponse(mohseniStudentSearchData);

    }

    public void sendMasterTemporaryScores(Integer courseId){
        MasterTemporaryScoreData masterTemporaryScoreData = MasterTemporaryScoreData.getMasterTemporaryScoreData(clientHandler,courseId);
        sendResponse(masterTemporaryScoreData);
    }

    public void sendEducationalSituationToStudent(){
        EducationalSituation educationalSituation = EducationalSituation.getEducationalSituationToStudent(clientHandler);
        sendResponse(educationalSituation);
    }

    public void sendEducationalSituationToAssistant(int studentId){ //name or studentShipNumber
        //TODO only his own department
        EducationalSituation educationalSituation = EducationalSituation.getEducationalSituationToAssistant(studentId);
        sendResponse(educationalSituation);
    }


    public void sendTATemporaryScores(String courseId,String masterId,String studentId){
        //TODO ONLY HIS OWN DEPARTMENT!!!!!!!!
        AssistantTemporaryScoreData assistantTemporaryScoreData = AssistantTemporaryScoreData.getAssistantTemporaryScoreData(clientHandler,courseId,masterId,studentId);
        sendResponse(assistantTemporaryScoreData);
    }

    public void sendMastersListData(String departmentId, String name, String masterGrade){
        MasterListData masterListData = MasterListData.getMasterListData(departmentId,name,masterGrade);
        sendResponse(masterListData);
    }

    public void sendCoursesListData(String courseNumber,String unit,String departmentId){
        CourseListData courseListData = CourseListData.getCourseListData(courseNumber,unit,departmentId);
        sendResponse(courseListData);
    }



    public void sendSuggestedCoursesData(){
        SuggestedCoursesData suggestedCoursesData = SuggestedCoursesData.getSuggestedCoursesData(clientHandler);
        sendResponse(suggestedCoursesData);
    }

    public void sendOfflineData() {
        OfflineData offlineData = OfflineData.getOfflineData(clientHandler);
        sendResponse(offlineData);
    }
}
