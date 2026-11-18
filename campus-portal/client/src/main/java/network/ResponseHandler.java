package network;

import GuiControllers.Gui;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import logic.pageDataClasses.AuthToken;
import logic.pageDataClasses.ErrorData;
import logic.pageDataClasses.OfflineData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseHandler extends Thread {
    static Logger log = LogManager.getLogger(ResponseHandler.class);

    private final Gson gson = Protocol.GSON;
    private final BufferedReader reader;
    private final Client client;
    private final Socket socket;

    public ResponseHandler(Client client) throws IOException {
        super("server-responses");
        this.client = client;
        socket = client.socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            String input;
            while ((input = Protocol.readLine(reader)) != null) {
                handle(input);
            }
        } catch (IOException e) {
            log.info("Connection ended: " + e.getMessage());
        }
        client.connectionLost(socket);
        log.info("Server disconnected.");
    }

    private void handle(String input) throws IOException {
        Response response = gson.fromJson(input, Response.class);
        log.debug("Response from server: " + response.getResponseType());
        switch (response.getResponseType()) {
            case OFFLINE_DATA -> saveOfflineData(gson.fromJson(input, OfflineData.class));
            case AUTH_TOKEN -> {
                client.setAuthToken(gson.fromJson(input, AuthToken.class).getAuthToken());
                client.requester.requestPageData(RequestType.LOGIN_DATA);
            }
            case ERROR -> showError(gson.fromJson(input, ErrorData.class).getMessage());
            default -> showOnCurrentScreen(input);
        }
    }

    private void showOnCurrentScreen(String input) {
        Gui screen = client.controller;
        if (screen != null) {
            Platform.runLater(() -> {
                try {
                    screen.update(input);
                } catch (RuntimeException e) {
                    log.error("Could not show a response on " + screen.getClass().getSimpleName(), e);
                }
            });
        }
    }

    private static void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, message);
            alert.setHeaderText(null);
            alert.show();
        });
    }

    private void saveOfflineData(OfflineData offlineData) throws IOException {
        saveData(offlineData.getMessengerData(), "MessengerData");
        saveData(offlineData.getMainMenuDataStr(), "MainMenuData");
        saveData(offlineData.getExamsListData(), "ExamsListData");
        saveData(offlineData.getWeeklyPlanData(), "WeeklyPlanData");
        saveData(offlineData.getEducationalSituation(), "EducationalSituation");
        int counter = 1;
        for (String chat : offlineData.getChats()) {
            saveData(chat, "chat" + counter++);
        }
    }

    private static void saveData(String content, String name) throws IOException {
        if (content != null) {
            Files.writeString(Path.of(OfflineStore.path(name + ".json")), content);
        }
    }
}
