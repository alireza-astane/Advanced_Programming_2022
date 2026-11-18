package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.pageDataClasses.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class OfflineRequestHandler {
    static Logger log = LogManager.getLogger(OfflineRequestHandler.class);
    WeeklyPlanData weeklyPlanData;
    ExamsListData examsListData;
    EducationalSituation educationalSituation;
    MessengerData messengerData;
    MainMenuData mainMenuDataStr;
    ArrayList<ChatData> chats;

    public void load(){
        try {
            this.educationalSituation=loadEducationalSituation();
        } catch (FileNotFoundException ignored) {

        }
        try {
            this.chats=loadChats();
        } catch (FileNotFoundException ignored) {

        }
        try {
            this.examsListData=loadExamListData();
        } catch (FileNotFoundException ignored) {

        }
        try {
            this.mainMenuDataStr=loadMainMenuData();
        } catch (FileNotFoundException ignored) {

        }
        try {
            this.messengerData=loadMessengerData();
        } catch (FileNotFoundException ignored) {

        }
        try {
            this.weeklyPlanData=loadWeeklyPlanData();
        } catch (FileNotFoundException ignored) {

        }

    }

    public WeeklyPlanData loadWeeklyPlanData() throws FileNotFoundException {
        GsonBuilder builder = Protocol.GSON.newBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(
                new FileReader(OfflineStore.path("WeeklyPlanData.json")));
        return gson.fromJson(bufferedReader, WeeklyPlanData.class);
    }

    public ExamsListData loadExamListData() throws FileNotFoundException {
        GsonBuilder builder = Protocol.GSON.newBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(
                new FileReader(OfflineStore.path("ExamsListData.json")));
        return gson.fromJson(bufferedReader, ExamsListData.class);
    }


    public EducationalSituation loadEducationalSituation() throws FileNotFoundException {
        GsonBuilder builder = Protocol.GSON.newBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(
                new FileReader(OfflineStore.path("EducationalSituation.json")));
        return gson.fromJson(bufferedReader, EducationalSituation.class);
    }

    public MessengerData loadMessengerData() throws FileNotFoundException {
        GsonBuilder builder = Protocol.GSON.newBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(
                new FileReader(OfflineStore.path("MessengerData.json")));
        return gson.fromJson(bufferedReader, MessengerData.class);
    }

    public MainMenuData loadMainMenuData() throws FileNotFoundException {
        GsonBuilder builder = Protocol.GSON.newBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(
                new FileReader(OfflineStore.path("MainMenuData.json")));
        return gson.fromJson(bufferedReader, MainMenuData.class);
    }

    public ArrayList<ChatData> loadChats() throws FileNotFoundException {
        ArrayList<ChatData> chatDatas = new ArrayList<>();
        for (int i = 0; i <messengerData.getChatsArraylist().size();i++) {
            GsonBuilder builder = Protocol.GSON.newBuilder();
            Gson gson = builder.create();
            BufferedReader bufferedReader = null;
            bufferedReader = new BufferedReader(
                    new FileReader(OfflineStore.path("chat" + i + ".json")));
            chatDatas.add(gson.fromJson(bufferedReader, ChatData.class));
        }
        return chatDatas;
    }
}
