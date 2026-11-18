package logic.pageDataClasses;

import logic.enums.*;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Score;

public class ProfileData extends Response {
    String nationalNumber;
    String userEmail;
    String userFullName;
    byte[] userImage;
    String lastLoginTime;
    String phoneNumber;
    String department;
    String entryYear;

    String studentShipNumber;
    String totalAverageScore;
    String superVisorName;
    StudentState studentState;
    StudentType studentType;

    String masterShipNumber;
    String masterRoomNumber;
    MasterGrade masterGrade;

    public static ProfileData getMasterProfileData(ClientHandler clientHandler) {
        ProfileData profileData = new ProfileData();
        profileData.setResponseType(ResponseType.MASTER_PROFILE_DATA);
        profileData.setNationalNumber(clientHandler.getUser().getUserNationalNumber());
        profileData.setUserEmail(clientHandler.getUser().getUserEmail());
        profileData.setUserImage(clientHandler.getUser().getUserImage());
        profileData.setLastLoginTime(clientHandler.getUser().getUserLastLoginTime().toString());
        profileData.setPhoneNumber(clientHandler.getUser().getUserPhoneNumber());
        profileData.setUserFullName(clientHandler.getUser().getUserFullName());

        profileData.setMasterGrade(clientHandler.getUser().getMaster().getMasterGrade());
        profileData.setMasterRoomNumber(clientHandler.getUser().getMaster().getMasterRoomNumber());
        profileData.setMasterShipNumber(clientHandler.getUser().getMaster().getMasterShipNumber());
        return profileData;
    }

    public static ProfileData getGeneralProfileData(ClientHandler clientHandler) {
        ProfileData profileData = new ProfileData();
        profileData.setResponseType(ResponseType.GENERAL_PROFILE_DATA);
        profileData.setNationalNumber(clientHandler.getUser().getUserNationalNumber());
        profileData.setUserEmail(clientHandler.getUser().getUserEmail());
        profileData.setUserImage(clientHandler.getUser().getUserImage());
        profileData.setLastLoginTime(clientHandler.getUser().getUserLastLoginTime().toString());
        profileData.setPhoneNumber(clientHandler.getUser().getUserPhoneNumber());
        profileData.setUserFullName(clientHandler.getUser().getUserFullName());
        return profileData;
    }

    public static ProfileData getStudentProfileData(ClientHandler clientHandler) {
        ProfileData profileData = new ProfileData();
        profileData.setResponseType(ResponseType.STUDENT_PROFILE_DATA);

        profileData.setNationalNumber(clientHandler.getUser().getUserNationalNumber());
        profileData.setUserEmail(clientHandler.getUser().getUserEmail());
        profileData.setUserImage(clientHandler.getUser().getUserImage());
        profileData.setLastLoginTime(clientHandler.getUser().getUserLastLoginTime().toString());
        profileData.setPhoneNumber(clientHandler.getUser().getUserPhoneNumber());
        profileData.setUserFullName(clientHandler.getUser().getUserFullName());


        profileData.setStudentType(clientHandler.getUser().getStudent().getStudentType());
        profileData.setStudentShipNumber(clientHandler.getUser().getStudent().getStudentshipNumber());
        profileData.setStudentState(clientHandler.getUser().getStudent().getStudentState());
        profileData.setSuperVisorName(DataManager.getUserById(clientHandler.getUser().getStudent().getStudentSupervisorId()).getUserFullName());
        profileData.setEntryYear(String.valueOf(clientHandler.getUser().getStudent().getEntryYear()));
        profileData.setDepartment(DataManager.getDepartmentById(clientHandler.getUser().getStudent().getStudentDepartmentId()).getDepartmentType().name);

        double sum = 0;
        int units = 0;

        for(Score score:clientHandler.getUser().getStudent().getStudentScores()){
            if(score.getScoreType()== ScoreType.VERIFIED){
                sum += score.getScoreValue();
                units += score.getUnits();
            }
        }
        profileData.setTotalAverageScore(String.valueOf(sum/units));
        return profileData;
    }


    public String getNationalNumber() {
        return nationalNumber;
    }

    public void setNationalNumber(String nationalNumber) {
        this.nationalNumber = nationalNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEntryYear() {
        return entryYear;
    }

    public void setEntryYear(String entryYear) {
        this.entryYear = entryYear;
    }

    public String getStudentShipNumber() {
        return studentShipNumber;
    }

    public void setStudentShipNumber(String studentShipNumber) {
        this.studentShipNumber = studentShipNumber;
    }

    public String getTotalAverageScore() {
        return totalAverageScore;
    }

    public void setTotalAverageScore(String totalAverageScore) {
        this.totalAverageScore = totalAverageScore;
    }

    public String getSuperVisorName() {
        return superVisorName;
    }

    public void setSuperVisorName(String superVisorName) {
        this.superVisorName = superVisorName;
    }

    public StudentState getStudentState() {
        return studentState;
    }

    public void setStudentState(StudentState studentState) {
        this.studentState = studentState;
    }

    public StudentType getStudentType() {
        return studentType;
    }

    public void setStudentType(StudentType studentType) {
        this.studentType = studentType;
    }

    public String getMasterShipNumber() {
        return masterShipNumber;
    }

    public void setMasterShipNumber(String masterShipNumber) {
        this.masterShipNumber = masterShipNumber;
    }

    public String getMasterRoomNumber() {
        return masterRoomNumber;
    }

    public void setMasterRoomNumber(String masterRoomNumber) {
        this.masterRoomNumber = masterRoomNumber;
    }

    public MasterGrade getMasterGrade() {
        return masterGrade;
    }

    public void setMasterGrade(MasterGrade masterGrade) {
        this.masterGrade = masterGrade;
    }
}
