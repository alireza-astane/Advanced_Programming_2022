package logic.pageDataClasses;

import logic.enums.MasterGrade;
import logic.enums.MasterType;
import logic.enums.ResponseType;
import logic.enums.UserType;

import network.Response;



public class EditMasterData extends Response {
    MasterType masterType;
    MasterGrade masterGrade;
    String masteryNumber;
    String masterRoomNumber;
    int masterDepartmentId;

    String username;
    String userFullName;
    UserType userType;
    byte[] userImage;
    String userEmail;
    String userPhoneNumber;
    String userNationalNumber;



    public MasterType getMasterType() {
        return masterType;
    }

    public void setMasterType(MasterType masterType) {
        this.masterType = masterType;
    }

    public MasterGrade getMasterGrade() {
        return masterGrade;
    }

    public void setMasterGrade(MasterGrade masterGrade) {
        this.masterGrade = masterGrade;
    }

    public String getMasteryNumber() {
        return masteryNumber;
    }

    public void setMasteryNumber(String masteryNumber) {
        this.masteryNumber = masteryNumber;
    }

    public String getMasterRoomNumber() {
        return masterRoomNumber;
    }

    public void setMasterRoomNumber(String masterRoomNumber) {
        this.masterRoomNumber = masterRoomNumber;
    }

    public int getMasterDepartmentId() {
        return masterDepartmentId;
    }

    public void setMasterDepartmentId(int masterDepartmentId) {
        this.masterDepartmentId = masterDepartmentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public byte[] getUserImage() {
        return userImage;
    }

    public void setUserImage(byte[] userImage) {
        this.userImage = userImage;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserNationalNumber() {
        return userNationalNumber;
    }

    public void setUserNationalNumber(String userNationalNumber) {
        this.userNationalNumber = userNationalNumber;
    }


}
