package logic.pageDataClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.enums.ResponseType;
import logic.enums.UserType;
import network.ClientHandler;
import network.Response;
import serverDataBase.models.Chat;
import serverDataBase.models.User;

import java.util.ArrayList;
import java.util.Arrays;

public class OfflineData extends Response {
    static GsonBuilder gsonBuilder = new GsonBuilder();
    static Gson gson = gsonBuilder.create();
    String mainMenuDataStr;
    String weeklyPlanData;
    String examsListData;
    String educationalSituation;
    String messengerData;
    ArrayList<String> chats;
    public static OfflineData getOfflineData(ClientHandler clientHandler) {
        OfflineData offlineData = new OfflineData();
        offlineData.setResponseType(ResponseType.OFFLINE_DATA);
        offlineData.chats = new ArrayList<>();
        for(Chat chat:clientHandler.getUser().getUserChats()){
            offlineData.chats.add(gson.toJson(ChatData.getChatData(clientHandler,chat.getChatId())));
        }
        MainMenuData mainMenuData = switch (clientHandler.getUser().getUserType()){
            case MASTER -> MainMenuData.getMasterMainMenuData(clientHandler);
            case ADMIN, MOHSENI -> null;
            case STUDENT -> MainMenuData.getStudentMainMenuData(clientHandler);
            case ASSISTANT -> MainMenuData.getAssistantMainMenuData(clientHandler);
        };
        offlineData.mainMenuDataStr = gson.toJson(mainMenuData);
        offlineData.messengerData = gson.toJson(MessengerData.getMessengerMenuData(clientHandler));
        offlineData.weeklyPlanData = gson.toJson(WeeklyPlanData.getWeeklyPlanData(clientHandler));

        switch (clientHandler.getUser().getUserType()){
            case STUDENT -> {
                offlineData.examsListData = gson.toJson(ExamsListData.getExamsListData(clientHandler));
                offlineData.educationalSituation = gson.toJson(EducationalSituation.getEducationalSituationToStudent(clientHandler));

            }
            case MASTER, ASSISTANT -> {
                offlineData.examsListData = gson.toJson(ExamsListData.getExamsListData(clientHandler));

            }
        }

        return offlineData;



    }
}
