package logic.pageDataClasses;

import logic.enums.ResponseType;
import logic.enums.StudentState;
import logic.enums.StudentType;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.User;

import java.util.ResourceBundle;


public class MohseniStudentInfoData extends Response {
    String nationalNumber;
    String userEmail;
    String userFullName;
    byte[] userImage;
    String lastLoginTime;  //?
    String phoneNumber;
    String department;
    String entryYear;
    String studentShipNumber;
    String totalAverageScore;
    String superVisorName;
    StudentState studentState;

    public static MohseniStudentInfoData getMohseniStudentInfoData(int studentUserId) {
        MohseniStudentInfoData mohseniStudentInfoData = new MohseniStudentInfoData();
        mohseniStudentInfoData.setResponseType(ResponseType.MOHSENI_STUDENT_INFO);
        User student = DataManager.getUserById(studentUserId);
        mohseniStudentInfoData.setStudentState(student.getStudent().getStudentState());
        mohseniStudentInfoData.setStudentType(student.getStudent().getStudentType());
        mohseniStudentInfoData.setDepartment(DataManager.getDepartmentById(student.getStudent().getStudentDepartmentId()).getDepartmentType().name);
        mohseniStudentInfoData.setEntryYear(String.valueOf(student.getStudent().getEntryYear()));
        mohseniStudentInfoData.setStudentShipNumber(student.getStudent().getStudentshipNumber());
        mohseniStudentInfoData.setLastLoginTime(student.getUserLastLoginTime().toString());
        mohseniStudentInfoData.setNationalNumber(student.getUserNationalNumber());
        mohseniStudentInfoData.setPhoneNumber(student.getUserPhoneNumber());
        mohseniStudentInfoData.setSuperVisorName(DataManager.getUserById(student.getStudent().getStudentSupervisorId()).getUserFullName());
        mohseniStudentInfoData.setUserImage(student.getUserImage());
        return mohseniStudentInfoData;
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

    StudentType studentType;


}
