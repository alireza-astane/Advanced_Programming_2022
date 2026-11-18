package GuiControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.AnimationTimer;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class MasterMenuGui extends Gui implements Initializable {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    MainMenuData mainMenuData;
    @FXML
    AnchorPane BackGround;

    @FXML
    Button MainMenuButton;

    @FXML
    Label UserFullName;

    @FXML
    Label UserEmail;

    @FXML
    Label CurrentTime;

    @FXML
    Button EducationButton;

    @FXML
    Button ExitButton;

    @FXML
    Label LastLoginTime;

    @FXML
    Button ProfileButton;

    @FXML
    Button RegistrationButton;

    @FXML
    Button RecordButton;

    @FXML
    ImageView UserImage;

    public void goExit(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }


    public void goRecord(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.RECORD);
    }

    public void goEducation(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.EDUCATION);
    }

    public void goProfile(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MASTER_PROFILE);
    }

    public void goRegistration(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.REGISTRATION);

    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.MASTER_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_MAIN_MENU));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                CurrentTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));}};
        timer.start();

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
                File outputFile = new File("src//main//resources//Image//Captcha//userImage.jpg");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(outputFile);
                    fos.write(mainMenuData.getUserImage());
                    fos.flush();
                    fos.close();
                    InputStream inputStream = new FileInputStream(outputFile);
                    UserImage.setImage(new Image(inputStream));
                } catch (IOException ignored) {}

            }
        });


    }
}

