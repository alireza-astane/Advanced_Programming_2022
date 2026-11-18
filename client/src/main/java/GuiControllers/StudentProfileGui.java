package GuiControllers;

import network.Protocol;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import logic.pageDataClasses.ProfileData;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;


public class StudentProfileGui extends Gui implements Initializable {
    ProfileData profileData;

    GsonBuilder gsonBuilder = Protocol.GSON.newBuilder();
    Gson gson = gsonBuilder.create();
    @FXML
    ColorPicker ColorPicker;
    @FXML
    AnchorPane BackGround;

    @FXML
    TextField PhoneNumberField;

    @FXML
    Button EditButton;

    @FXML
    TextField EmailField;

    @FXML
    Label EntryYearLabel;

    @FXML
    Label GradeLabel;

    @FXML
    Label StateLabel;

    @FXML
    Label SupervisorLabel;

    @FXML
    ImageView UserImage;
    
    @FXML
    Label DepartmentLabel;
    
    @FXML
    Button ExitButton;

    @FXML
    Label FullNameLabel;

    @FXML
    Button MainMenuButton;

    @FXML
    Label MeanScoreLabel;
    
    @FXML
    Label NationalNumberLabel;

    @FXML
    Label StudentShipNumber;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"),null,null)));
        Client.getInstance().controller=this;
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_PROFILE_DATA));
    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    public void doEdit(ActionEvent actionEvent) {
        if(!Objects.equals(EmailField.getText(), profileData.getUserEmail())){
            Request request = new Request(RequestType.CHANGE_EMAIL);
            request.setArrayList(new ArrayList<>(Collections.singletonList(EmailField.getText())));
            Client.getInstance().getRequester().sendRequest(request);
        }
        if(!Objects.equals(PhoneNumberField.getText(), profileData.getPhoneNumber())){
            Request request = new Request(RequestType.CHANGE_PHONE_NUMBER);
            request.setArrayList(new ArrayList<>(Collections.singletonList(PhoneNumberField.getText())));
            Client.getInstance().getRequester().sendRequest(request);
    }}

    public void doChangeBackGround(ActionEvent actionEvent) {
        //Gui.color = ColorPicker.getValue();
        //BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));
    }

    @Override
    public void update(String input) {
        profileData = gson.fromJson(input, ProfileData.class);

        try {
            PhoneNumberField.setText(profileData.getPhoneNumber());
        } catch (Exception ignored) {}

        try {
            EmailField.setText(profileData.getUserEmail());
        } catch (Exception ignored) {}

        try {
            EntryYearLabel.setText(profileData.getEntryYear());
        } catch (Exception ignored) {}

        try {
            GradeLabel.setText(profileData.getStudentType().toString());
        } catch (Exception ignored) {}

        try {
            StateLabel.setText(profileData.getStudentState().toString());
        } catch (Exception ignored) {}

        try {
            SupervisorLabel.setText(profileData.getSuperVisorName());
        } catch (Exception ignored) {}

        try {
            DepartmentLabel.setText(profileData.getDepartment());
        } catch (Exception ignored) {}

        try {
            FullNameLabel.setText(profileData.getUserFullName());
        } catch (Exception ignored) {}

        try {
            MeanScoreLabel.setText(profileData.getTotalAverageScore());
        } catch (Exception ignored) {}

        try {
            NationalNumberLabel.setText(profileData.getNationalNumber());
        } catch (Exception ignored) {}

        try {
            StudentShipNumber.setText(profileData.getStudentShipNumber());
        } catch (Exception ignored) {}

        try {
            UserImage.setImage(new Image(new java.io.ByteArrayInputStream(profileData.getUserImage())));
        } catch (Exception ignored) {}
    }
}
