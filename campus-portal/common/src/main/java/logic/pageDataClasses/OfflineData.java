package logic.pageDataClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.enums.ResponseType;
import network.Response;


import java.util.ArrayList;

import static logic.enums.UserType.MASTER;

public class OfflineData extends Response {
    static GsonBuilder gsonBuilder = new GsonBuilder();
    static Gson gson = gsonBuilder.create();
    public String mainMenuDataStr;

    public String getMainMenuDataStr() {
        return mainMenuDataStr;
    }

    public void setMainMenuDataStr(String mainMenuDataStr) {
        this.mainMenuDataStr = mainMenuDataStr;
    }

    public String getWeeklyPlanData() {
        return weeklyPlanData;
    }

    public void setWeeklyPlanData(String weeklyPlanData) {
        this.weeklyPlanData = weeklyPlanData;
    }

    public String getExamsListData() {
        return examsListData;
    }

    public void setExamsListData(String examsListData) {
        this.examsListData = examsListData;
    }

    public String getEducationalSituation() {
        return educationalSituation;
    }

    public void setEducationalSituation(String educationalSituation) {
        this.educationalSituation = educationalSituation;
    }

    public String getMessengerData() {
        return messengerData;
    }

    public void setMessengerData(String messengerData) {
        this.messengerData = messengerData;
    }

    public ArrayList<String> getChats() {
        return chats;
    }

    public void setChats(ArrayList<String> chats) {
        this.chats = chats;
    }

    public String weeklyPlanData;
    public String examsListData;
    public String educationalSituation;
    public String messengerData;
    public ArrayList<String> chats;
}
