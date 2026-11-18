package logic.pageBuilders;

import logic.enums.ResponseType;
import logic.enums.StudentState;
import logic.enums.UserType;
import logic.pageDataClasses.*;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;

public final class MainMenuDataBuilder {

    private MainMenuDataBuilder() {
    }

    public static MainMenuData getStudentMainMenuData(ClientHandler clientHandler) {
        MainMenuData mainMenuData = new MainMenuData();

        mainMenuData.setResponseType(ResponseType.STUDENT_MAIN_MENU);

        mainMenuData.setUserType(clientHandler.getUser().getUserType());
    
        mainMenuData.setStudentState(clientHandler.getUser().getStudent().getStudentState());

    
        mainMenuData.setLastLoginTime(clientHandler.getUser().getUserLastLoginTime().toString());
    
        mainMenuData.setRegistrationTime(clientHandler.getUser().getStudent().getStudentRegistrationTime().toString());
    
        mainMenuData.setUserEmail(clientHandler.getUser().getUserEmail());
    
        mainMenuData.setSupervisorName(DataManager.getUserById(clientHandler.getUser().getStudent().getStudentSupervisorId()).getUserFullName());

    
        mainMenuData.setUserImage(clientHandler.getUser().getUserImage());
    
        mainMenuData.setUserFullName(clientHandler.getUser().getUserFullName());
    







        return mainMenuData;
    }

    public static MainMenuData getMasterMainMenuData(ClientHandler clientHandler) {
        MainMenuData  mainMenuData = new MainMenuData();
        mainMenuData.setResponseType(ResponseType.MASTER_MAIN_MENU);
        mainMenuData.setUserType(clientHandler.getUser().getUserType());
        mainMenuData.setUserFullName(clientHandler.getUser().getUserFullName());
        mainMenuData.setUserImage(clientHandler.getUser().getUserImage());
        mainMenuData.setUserEmail(clientHandler.getUser().getUserEmail());

        return mainMenuData;
    }

    public static MainMenuData getAssistantMainMenuData(ClientHandler clientHandler){
        MainMenuData mainMenuData = new MainMenuData();
        mainMenuData.setResponseType(ResponseType.ASSISTANT_MAIN_MENU);
        mainMenuData.setUserFullName(clientHandler.getUser().getUserFullName());
        mainMenuData.setUserImage(clientHandler.getUser().getUserImage());
        mainMenuData.setUserEmail(clientHandler.getUser().getUserEmail());
        return mainMenuData;
    }
}
