package logic.pageBuilders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import logic.enums.ResponseType;
import logic.enums.UserType;
import logic.pageDataClasses.*;
import network.Protocol;
import network.ClientHandler;
import network.Response;
import serverDataBase.models.Chat;
import serverDataBase.models.User;

public final class OfflineDataBuilder {

    private OfflineDataBuilder() {
    }

    public static OfflineData getOfflineData(ClientHandler clientHandler) {
        OfflineData offlineData = new OfflineData();
        offlineData.setResponseType(ResponseType.OFFLINE_DATA);
        offlineData.chats = new ArrayList<>();
        for(Chat chat:clientHandler.getUser().getUserChats()){
            offlineData.chats.add(Protocol.GSON.toJson(ChatDataBuilder.getChatData(clientHandler,chat.getChatId())));
        }
        MainMenuData mainMenuData = switch (clientHandler.getUser().getUserType()){
            case MASTER -> MainMenuDataBuilder.getMasterMainMenuData(clientHandler);
            case ADMIN, MOHSENI -> null;
            case STUDENT -> MainMenuDataBuilder.getStudentMainMenuData(clientHandler);
            case ASSISTANT -> MainMenuDataBuilder.getAssistantMainMenuData(clientHandler);
        };
        offlineData.mainMenuDataStr = Protocol.GSON.toJson(mainMenuData);
        offlineData.messengerData = Protocol.GSON.toJson(MessengerDataBuilder.getMessengerMenuData(clientHandler));
        offlineData.weeklyPlanData = Protocol.GSON.toJson(WeeklyPlanDataBuilder.getWeeklyPlanData(clientHandler));

        switch (clientHandler.getUser().getUserType()){
            case STUDENT -> {
                offlineData.examsListData = Protocol.GSON.toJson(ExamsListDataBuilder.getExamsListData(clientHandler));
                offlineData.educationalSituation = Protocol.GSON.toJson(EducationalSituationBuilder.getEducationalSituationToStudent(clientHandler));

            }
            case MASTER, ASSISTANT -> {
                offlineData.examsListData = Protocol.GSON.toJson(ExamsListDataBuilder.getExamsListData(clientHandler));

            }
        }

        return offlineData;



    }
}
