package logic.pageBuilders;

import java.util.ResourceBundle;
import logic.enums.ResponseType;
import logic.enums.StudentState;
import logic.enums.StudentType;
import logic.pageDataClasses.*;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.User;

public final class MohseniStudentInfoDataBuilder {

    private MohseniStudentInfoDataBuilder() {
    }

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
}
