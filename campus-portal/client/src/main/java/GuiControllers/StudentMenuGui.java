package GuiControllers;

import network.Protocol;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import logic.pageDataClasses.LoginData;
import logic.pageDataClasses.MainMenuData;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;



public class StudentMenuGui extends Gui implements Initializable {
    public Button CWButton;
    public Button MessengerButton;
    public Button MainMenuButton;
    public Button sendbutton;
    public SplitMenuButton departmentchoice;
    public TextArea text;
    public SplitMenuButton gradechoice;
    public TextField entryyearfield;
    GsonBuilder gsonBuilder = Protocol.GSON.newBuilder();
    Gson gson = gsonBuilder.create();
    MainMenuData mainMenuData;
    public Label RegistrationTimeLabel;
    @FXML
    AnchorPane BackGround;

    @FXML
    Label CurrentTime;

    @FXML
    Button EducationButton;

    @FXML
    Button ExitButton;

    @FXML
    Label LabelNUm4_1;

    @FXML
    Label LabelNum1_1;

    @FXML
    Label LabelNum2_1;

    @FXML
    Label LastLoginTime;

    @FXML
    Button ProfileButton;

    @FXML
    Button RegistrationButton;

    @FXML
    Label UserEmail;

    @FXML
    Label UserFullName;

    @FXML
    ImageView UserImage;

    @FXML
    Label headLabel;

    @FXML
    Label EducationalSituationLabel;

    @FXML
    Label HasRegistrationLicenseLabel;

    @FXML
    Label HasRegistrationTimeLabel;

    @FXML
    Label HasSupervisorLabel;

    @FXML
    Button RecordButton;

    @FXML
    Label RegistrationLicenseLabel;

    @FXML
    Label RegistrationTimelabel;

    @FXML
    Label SupervisorNameLabel;


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
        Gui.setStage(stage, FxmlFiles.STUDENT_PROFILE);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_PROFILE_DATA));}


    public void goRegistration(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.REGISTRATION);

    }

    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"),null,null)));
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                CurrentTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
        };
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
                    if(mainMenuData.getUserImage()!=null){
                    fos = new FileOutputStream(outputFile);
                    fos.write(mainMenuData.getUserImage());
                    fos.flush();
                    fos.close();
                    InputStream inputStream = new FileInputStream(outputFile);
                    UserImage.setImage(new Image(inputStream));}
                } catch (IOException ignored) {}

                try{RegistrationTimelabel.setText(mainMenuData.getRegistrationTime());} catch (Exception ignored) {}
                try{EducationalSituationLabel.setText(mainMenuData.getStudentState().toString());} catch (Exception ignored) {}
                try{SupervisorNameLabel.setText(mainMenuData.getSupervisorName());} catch (Exception ignored) {}

            }
        });

    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    public void goMessenger(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.MESSENGER_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MESSENGER_MENU));
    }
}


