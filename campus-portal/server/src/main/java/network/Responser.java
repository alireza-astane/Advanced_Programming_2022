package network;

import logic.Configuration;
import logic.enums.ResponseType;
import logic.pageBuilders.*;
import logic.pageDataClasses.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import serverDataBase.DataManager;
import serverDataBase.models.User;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Responser {

    static Logger log = LogManager.getLogger(Responser.class);
    private final PrintWriter printWriter;
    private final ClientHandler clientHandler;

    public Responser(ClientHandler clientHandler) throws IOException {
        this.clientHandler = clientHandler;
        printWriter = new PrintWriter(new OutputStreamWriter(
                clientHandler.socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public synchronized void sendResponse(Response response) {
        log.debug("Response " + response.getResponseType() + " sent to client " + clientHandler.getId());
        printWriter.print(Protocol.GSON.toJson(response));
        printWriter.print('\n');
        printWriter.flush();
    }

    public void sendError(String message) {
        sendResponse(new ErrorData(message));
    }

    public void sendAuthToken(String authToken) {
        sendResponse(new AuthToken(authToken));
    }

    public void sendLoginData() {
        sendResponse(LoginDataBuilder.getLoginData(clientHandler));
    }

    public void sendLoginResult(ArrayList<String> arrayList) {
        LoginData loginData = LoginDataBuilder.getLoginData(clientHandler, arrayList.get(2));
        if (loginData.getResult() == null) {
            User user = DataManager.getUserByUserName(arrayList.get(0));
            if (user == null || !Passwords.matches(arrayList.get(1), user.getUserPassword())) {
                loginData.setResult("Wrong username or password");
            } else if (!user.isCanLogin()) {
                loginData.setResult("This account cannot log in");
            } else {
                loginData.setResult(logIn(user));
            }
        }
        sendResponse(loginData);
    }

    private String logIn(User user) {
        clientHandler.setUser(user);
        long hours = Duration.between(user.getUserLastLoginTime(), LocalDateTime.now()).toHours();
        if (hours > Integer.parseInt(Configuration.logics.getProperty("loginTimeDifference"))) {
            clientHandler.setMustChangePassword(true);
            return "ChangePass";
        }
        DataManager.setLastloginTime(user.getId());
        return switch (user.getUserType()) {
            case ASSISTANT -> "AssistantMenu";
            case MASTER -> "MasterMenu";
            case STUDENT -> "StudentMenu";
            case ADMIN -> "AdminMenu";
            case MOHSENI -> "MohseniMenu";
        };
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
        MainMenuData mainMenuData = MainMenuDataBuilder.getStudentMainMenuData(clientHandler);
        sendResponse(mainMenuData);
    }

    public void sendMasterMenuData(){
        MainMenuData mainMenuData = MainMenuDataBuilder.getMasterMainMenuData(clientHandler);
        sendResponse(mainMenuData);
    }

    public void sendAssistantMenuData(){
        MainMenuData mainMenuData = MainMenuDataBuilder.getAssistantMainMenuData(clientHandler);
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
        WeeklyPlanData weeklyPlanData = WeeklyPlanDataBuilder.getWeeklyPlanData(clientHandler);
        sendResponse(weeklyPlanData);
    }

    public void sendExamsListData(){
        ExamsListData examsListData = ExamsListDataBuilder.getExamsListData(clientHandler);

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
        ChatData chatData = ChatDataBuilder.getChatData(clientHandler,chatId);
        sendResponse(chatData);
    }

    public void sendMessengerMenuData(){
        MessengerData messengerData = MessengerDataBuilder.getMessengerMenuData(clientHandler);
        sendResponse(messengerData);
    }

    public void sendCWMenuData(){
        CWMenuData cwMenuData = CWMenuDataBuilder.getCwMenuData(clientHandler);
        sendResponse(cwMenuData);
    }

    public void sendCWCourseData(int courseId){
        CWCoursePageData cwCoursePageData = CWCoursePageDataBuilder.getCWCourseData(courseId);
        sendResponse(cwCoursePageData);
    }

    public void sendMasterProfileData(){
        ProfileData profileData = ProfileDataBuilder.getMasterProfileData(clientHandler);
        sendResponse(profileData);
    }

    public void sendGeneralProfileData(){
        ProfileData profileData = ProfileDataBuilder.getGeneralProfileData(clientHandler);
        sendResponse(profileData);
    }

    public void sendStudentProfileData(){
        ProfileData profileData = ProfileDataBuilder.getStudentProfileData(clientHandler);
        sendResponse(profileData);
    }

    public void sendChangePassData(){
        Response response = new Response();
        response.setResponseType(ResponseType.CHANGE_PASS_DATA);
        sendResponse(response);
    }

    public void senCWEducationalContextData(int cwEducationalContextId){
        CWEducationalContextData cwEducationalContextData = CWEducationalContextDataBuilder.getCWEducationalContextData(cwEducationalContextId);
        sendResponse(cwEducationalContextData);
    }

    public void sendAddChatData(){
        AddChatData addChatData = AddChatDataBuilder.getAddChatData(clientHandler);
        sendResponse(addChatData);
    }

    public void sendMohseniStudentInfoData(String studentUserId){
        MohseniStudentInfoData mohseniStudentInfoData = MohseniStudentInfoDataBuilder.getMohseniStudentInfoData(Integer.parseInt(studentUserId));
        sendResponse(mohseniStudentInfoData);
    }

    public void sendUniRequestsData(){
        UniRequestsData uniRequestsData = UniRequestsDataBuilder.getUniRequestData(clientHandler);
        sendResponse(uniRequestsData);
    }

    public void sendStudentTemporaryScores(){
        StudentTemporaryScoreData studentTemporaryScoreData= StudentTemporaryScoreDataBuilder.getStudentTemporaryScoreData(clientHandler);
        sendResponse(studentTemporaryScoreData);
    }



    public void sendStudentGeneralCalenderData(){
        GeneralCalenderData generalCalenderData = GeneralCalenderDataBuilder.getStudentGeneralCalenderData(clientHandler);
        sendResponse(generalCalenderData);
    }

    public void sendMasterGeneralCalenderData(){
        GeneralCalenderData generalCalenderData = GeneralCalenderDataBuilder.getMasterGeneralCalenderData(clientHandler);
        sendResponse(generalCalenderData);
    }



    public void sendEditMasterData(int masterId){
        EditMasterData editMasterData =EditMasterDataBuilder.getEditMasterData(masterId);
        sendResponse(editMasterData);
    }

    public void sendCWStudentExerciseData(int exerciseId){
        CWStudentExerciseData cwStudentExerciseData =CWStudentExerciseDataBuilder.getCWStudentExerciseData(clientHandler,exerciseId);
        sendResponse(cwStudentExerciseData);
    }

    public void sendEditCourseData(int courseId){
        EditCourseData editCourseData = EditCourseDataBuilder.getEditCourseData(courseId);
        sendResponse(editCourseData);

    }
    public void sendCWMasterExerciseData(int exerciseId){
        CWMasterExerciseData cwMasterExerciseData =CWMasterExerciseDataBuilder.getCWMasterExerciseData(exerciseId);
        sendResponse(cwMasterExerciseData);
    }

    public void sendMohseniMessengerData(){
        MohseniMessengerData mohseniMessengerData = new MohseniMessengerData();
        sendResponse(mohseniMessengerData);
    }



    public void sendMohseniStudentSearchData(String departmentId, String entryYear, String studentType){
        MohseniStudentSearchData mohseniStudentSearchData = MohseniStudentSearchDataBuilder.getMohseniStudentSearchData(departmentId,entryYear,studentType);
        sendResponse(mohseniStudentSearchData);

    }

    public void sendMasterTemporaryScores(Integer courseId){
        MasterTemporaryScoreData masterTemporaryScoreData = MasterTemporaryScoreDataBuilder.getMasterTemporaryScoreData(clientHandler,courseId);
        sendResponse(masterTemporaryScoreData);
    }

    public void sendEducationalSituationToStudent(){
        EducationalSituation educationalSituation = EducationalSituationBuilder.getEducationalSituationToStudent(clientHandler);
        sendResponse(educationalSituation);
    }

    public void sendEducationalSituationToAssistant(int studentId){ //name or studentShipNumber
        //TODO only his own department
        EducationalSituation educationalSituation = EducationalSituationBuilder.getEducationalSituationToAssistant(studentId);
        sendResponse(educationalSituation);
    }


    public void sendTATemporaryScores(String courseId,String masterId,String studentId){
        //TODO ONLY HIS OWN DEPARTMENT!!!!!!!!
        AssistantTemporaryScoreData assistantTemporaryScoreData = AssistantTemporaryScoreDataBuilder.getAssistantTemporaryScoreData(clientHandler,courseId,masterId,studentId);
        sendResponse(assistantTemporaryScoreData);
    }

    public void sendMastersListData(String departmentId, String name, String masterGrade){
        MasterListData masterListData = MasterListDataBuilder.getMasterListData(departmentId,name,masterGrade);
        sendResponse(masterListData);
    }

    public void sendCoursesListData(String courseNumber,String unit,String departmentId){
        CourseListData courseListData = CourseListDataBuilder.getCourseListData(courseNumber,unit,departmentId);
        sendResponse(courseListData);
    }



    public void sendSuggestedCoursesData(){
        SuggestedCoursesData suggestedCoursesData = SuggestedCoursesDataBuilder.getSuggestedCoursesData(clientHandler);
        sendResponse(suggestedCoursesData);
    }

    public void sendOfflineData() {
        OfflineData offlineData = OfflineDataBuilder.getOfflineData(clientHandler);
        sendResponse(offlineData);
    }
}
