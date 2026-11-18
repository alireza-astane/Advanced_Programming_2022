package logic.pageBuilders;

import logic.enums.*;
import logic.pageDataClasses.*;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Score;

public final class ProfileDataBuilder {

    private ProfileDataBuilder() {
    }

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
}
