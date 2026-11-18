package network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import logic.pageDataClasses.AuthToken;
import logic.pageDataClasses.MainMenuData;
import logic.pageDataClasses.MessengerData;
import logic.pageDataClasses.OfflineData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ResponseHandler extends Thread{
    static GsonBuilder gsonBuilder = new GsonBuilder();
    static Gson gson = gsonBuilder.create();
    static Logger log = LogManager.getLogger(ResponseHandler.class);
    public  ResponseHandler(Client client) throws IOException {
        this.client=client;
        this.socket=client.socket;
        this.scanner = new Scanner(socket.getInputStream());
    }
    Socket socket;
    Scanner scanner;

    Client client;
    public File bytes2File(byte[] bytes){
        return null;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Waiting for Server Response");
        while(scanner.hasNext()){
        String input = scanner.nextLine();
        Response response = gson.fromJson(input, Response.class);
            log.info("Client got a Response from Server: "+response.getResponseType());
        switch (response.getResponseType()){
            case OFFLINE_DATA -> {
                try {
                    saveOfflineData((OfflineData)response);
                } catch (IOException ignored) {
                }


            }
            case AUTH_TOKEN -> {
                AuthToken authToken = gson.fromJson(input, AuthToken.class);
                client.setAuthToken(authToken.getAuthToken());
                log.info("Authentication completed");
                client.requester.requestPageData(RequestType.LOGIN_DATA);
            }
            default -> {
                Client.getInstance().controller.update(input);
            }
        }
    }
        //TODO go offline
        log.info("Server disconnected. Going Offline...");
    }

    public void saveOfflineData(OfflineData offlineData) throws IOException {
        saveData(offlineData.getMessengerData(), "MessengerData");
        saveData(offlineData.getMainMenuDataStr(),"MainMenuData");
        saveData(offlineData.getExamsListData(),"ExamsListData");
        saveData(offlineData.getWeeklyPlanData(),"WeaklyPlanData");
        saveData(offlineData.getEducationalSituation(),"EducationalSituation");
        int counter = 1;
        for(String chatStr: offlineData.getChats()){
            saveData(chatStr,"Chat"+counter);
        }


    }
    public void saveData(String str,String name) {
        try {
            String offlineDataPackage = System.getProperty("user.dir")+"//src//main//offlineDataBase//";
            FileWriter writer = new FileWriter(offlineDataPackage+name+".json");
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
