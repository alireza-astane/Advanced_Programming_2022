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
import logic.Configuration;
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
import java.util.*;

public class MasterProfileGui extends Gui implements Initializable {
    ProfileData profileData;

    GsonBuilder gsonBuilder = Protocol.GSON.newBuilder();
    Gson gson = gsonBuilder.create();
    @FXML
    javafx.scene.control.ColorPicker ColorPicker;
    @FXML
    AnchorPane BackGround;

    @FXML
    Label DepartmentLabel;

    @FXML
    Button EditButton;

    @FXML
    TextField EmailField;

    @FXML
    Button ExitButton;

    @FXML
   Label FullNameLabel;

    @FXML
    Label GradeLabel;

    @FXML
    Button MainMenuButton;

    @FXML
    Label MasteryNumber;

    @FXML
    Label NationalNumberLabel;

    @FXML
    TextField PhoneNumberField;

    @FXML
    Label RoomNumberLabel;

    @FXML
    ImageView UserImage;

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MASTER_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_MAIN_MENU));
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
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
        Client.getInstance().controller=this;
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_PROFILE_DATA));
    }

    public void doChangeBackGround(ActionEvent actionEvent) {
        //Gui.color = ColorPicker.getValue();
        //BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));
    }


    @Override
    public void update(String input) {
        profileData = gson.fromJson(input, ProfileData.class);

        try {
            NationalNumberLabel.setText(profileData.getNationalNumber());
        } catch (Exception ignored) {}

        try {
            MasteryNumber.setText(profileData.getMasterShipNumber());
        } catch (Exception ignored) {}

        try {
            UserImage.setImage(new Image(new java.io.ByteArrayInputStream(profileData.getUserImage())));
        } catch (Exception ignored) {}


        try {
            DepartmentLabel.setText(profileData.getDepartment());
        } catch (Exception ignored) {}

        try {
            FullNameLabel.setText(profileData.getUserFullName());
        } catch (Exception ignored) {}


        try {
            PhoneNumberField.setText(profileData.getPhoneNumber());
        } catch (Exception ignored) {}

        try {
            EmailField.setText(profileData.getUserEmail());
        } catch (Exception ignored) {}

        try {
            GradeLabel.setText(profileData.getMasterGrade().toString());
        } catch (Exception ignored) {}

        try {
            RoomNumberLabel.setText(profileData.getMasterRoomNumber());
        } catch (Exception ignored) {}
    }

}
