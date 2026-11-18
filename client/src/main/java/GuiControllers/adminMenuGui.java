package GuiControllers;

import network.Protocol;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import logic.pageDataClasses.MainMenuData;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class adminMenuGui extends Gui implements Initializable {
    public Button profileButton;
    GsonBuilder gsonBuilder = Protocol.GSON.newBuilder();
    Gson gson = gsonBuilder.create();
    MainMenuData mainMenuData;


    @FXML
    Label UserFullName;

    @FXML
    Label UserEmail;

    @FXML
    Label LastLoginTime;

    @FXML
    ImageView UserImage;

    @FXML
    Label CurrentTime;

    public AnchorPane BackGround;
    public Button ExitButton;
    public Button MainMenuButton;

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.ADMIN_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.ADMIN_MENU_DATA));
    }

    @Override
    public void update(String input) {
        mainMenuData = gson.fromJson(input, MainMenuData.class);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                try {
                    UserEmail.setText(mainMenuData.getUserEmail());
                } catch (Exception ignored) {}
                try {
                    UserFullName.setText(mainMenuData.getUserFullName());
                } catch (Exception ignored) {}
                try {
                    LastLoginTime.setText(mainMenuData.getLastLoginTime());
                } catch (Exception ignored) {}
                if (mainMenuData.getUserImage() != null) {
                    UserImage.setImage(new Image(new java.io.ByteArrayInputStream(mainMenuData.getUserImage())));
                }

            }
        });


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
    }

    public void goProfile(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.GENERAL_PROFILE);
    }
}
