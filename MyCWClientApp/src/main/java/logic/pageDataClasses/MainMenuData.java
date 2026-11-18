package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.StudentState;
import logic.enums.UserType;
import network.Response;


public class MainMenuData extends Response {

    UserType userType;

    String userEmail;
    String userFullName;
    byte[] userImage;
    String lastLoginTime;
    StudentState studentState;
    String SupervisorName;
    String RegistrationTime;




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
