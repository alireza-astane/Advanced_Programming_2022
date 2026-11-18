package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.StudentState;
import logic.enums.UserType;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;

public class MainMenuData extends Response {

    UserType userType;

    String userEmail;
    String userFullName;
    byte[] userImage;
    String lastLoginTime;
    StudentState studentState;
    String SupervisorName;
    String RegistrationTime;

    public static MainMenuData getStudentMainMenuData(ClientHandler clientHandler) {
        MainMenuData mainMenuData = new MainMenuData();

        mainMenuData.setResponseType(ResponseType.STUDENT_MAIN_MENU);

        try {
            mainMenuData.setUserType(clientHandler.getUser().getUserType());
        } catch (Exception ignored) {}
        try {
            mainMenuData.setStudentState(clientHandler.getUser().getStudent().getStudentState());

        } catch (Exception ignored) {}
        try {
            mainMenuData.setLastLoginTime(clientHandler.getUser().getUserLastLoginTime().toString());
        } catch (Exception ignored) {}
        try {
            mainMenuData.setRegistrationTime(clientHandler.getUser().getStudent().getStudentRegistrationTime().toString());
        } catch (Exception ignored) {}
        try {
            mainMenuData.setUserEmail(clientHandler.getUser().getUserEmail());
        } catch (Exception ignored) {}
        try {
            mainMenuData.setSupervisorName(DataManager.getUserById(clientHandler.getUser().getStudent().getStudentSupervisorId()).getUserFullName());

        } catch (Exception ignored) {}
        try {
            mainMenuData.setUserImage(clientHandler.getUser().getUserImage());
        } catch (Exception ignored) {}
        try {
            mainMenuData.setUserFullName(clientHandler.getUser().getUserFullName());
        } catch (Exception ignored) {}







        return mainMenuData;
    }

    public static MainMenuData getMasterMainMenuData(ClientHandler clientHandler) {
        MainMenuData  mainMenuData = new MainMenuData();
        mainMenuData.setResponseType(ResponseType.MASTER_MAIN_MENU);
        mainMenuData.setUserType(clientHandler.getUser().getUserType());
        mainMenuData.setUserFullName(clientHandler.getUser().getUserFullName());
        mainMenuData.setUserImage(clientHandler.getUser().getUserImage());
        mainMenuData.setUserEmail(clientHandler.getUser().getUserEmail());

        return mainMenuData;
    }


    public static MainMenuData getAssistantMainMenuData(ClientHandler clientHandler){
        MainMenuData mainMenuData = new MainMenuData();
        mainMenuData.setResponseType(ResponseType.ASSISTANT_MAIN_MENU);
        mainMenuData.setUserFullName(clientHandler.getUser().getUserFullName());
        mainMenuData.setUserImage(clientHandler.getUser().getUserImage());
        mainMenuData.setUserEmail(clientHandler.getUser().getUserEmail());
        return mainMenuData;
    }



    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public StudentState getStudentState() {
        return studentState;
    }

    public void setStudentState(StudentState studentState) {
        this.studentState = studentState;
    }

    public String getRegistrationTime() {
        return RegistrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        RegistrationTime = registrationTime;
    }

    public String getSupervisorName() {
        return SupervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        SupervisorName = supervisorName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

}
