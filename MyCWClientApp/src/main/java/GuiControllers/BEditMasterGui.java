package GuiControllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.Configuration;
import network.Client;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class BEditMasterGui extends  Gui  implements Initializable {
    @FXML
    Button LoadButton;
    //HashMap<String, MasterGrade> grades = new HashMap<>();

    File file1;
    @FXML
    Button EditButton;

    @FXML
    AnchorPane BackGround;

    @FXML
    Label DepartmentLabel;

    @FXML
    TextField EmailField;

    @FXML
    TextField RoomNumField;

    @FXML
    Label ErrorLabel;

    @FXML
    Button ExitButton;

    @FXML
    TextField FullNameField;

    @FXML
    TextField IdField;

    @FXML
    Button MainMenuButton;

    @FXML
    TextField MasteryNumberField;

    @FXML
    TextField NationalNumberField;

    @FXML
    TextField PassField;

    @FXML
    TextField PhoneNumberField;

    @FXML
    ChoiceBox<Object> GradeChoice;

    @FXML
    Button UploadImageButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
        //BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));
        //DepartmentLabel.setText(Load.loadDepartment(Gui.getUserId()).getDepartmentName());

        //grades.put("Assistant Prof.",MasterGrade.ASSISTANT_PROFESSOR);
        //grades.put("Associate Prof.",MasterGrade.ASSOCIATE_PROFESSOR);
        //grades.put("Post Doc.",MasterGrade.POSTDOC);
        //grades.put("Prof.",MasterGrade.PROFESSOR);
        //GradeChoice.setItems(FXCollections.observableArrayList(grades.keySet()));
    }

    public void doUploadImage(ActionEvent actionEvent) {
        FileChooser file = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("images", "*.jpg","*JPG","*png","*PNG");
        file.getExtensionFilters().add(extFilter);
        file.setTitle("Upload Image");
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        file1 = file.showOpenDialog(stage);
    }

    public void doEditMaster(ActionEvent actionEvent) { /*
        if(IdField.getText().equals("")||
                PassField.getText().equals("")||
                FullNameField.getText().equals("")||
                NationalNumberField.getText().equals("")||
                RoomNumField.getText().equals("")||
                MasteryNumberField.getText().equals("")||
                (GradeChoice.getValue()==null)||
                EmailField.getText().equals("")||
                (file1==null)){ErrorLabel.setText("Please Fill All Data");}
        else{
            if(Load.loadAllUsers().contains(IdField.getText())){ErrorLabel.setText("This Id Already Exits!");}
            else{
                ErrorLabel.setText("User Creation Was Successful");
                Request.requestAddNewMaster(IdField.getText(),PassField.getText(), FullNameField.getText(),
                        Gui.user.getUserDepartmentId(),NationalNumberField.getText(),
                        RoomNumField.getText(),MasteryNumberField.getText(),
                        grades.get(GradeChoice.getValue().toString()),EmailField.getText(),file1);
            } */

}

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        //Gui.goMainMenu(stage);
    }

    public void doLoadMaster(ActionEvent actionEvent) { /*
        User master = Load.loadUser(IdField.getText());
        PassField.setPromptText(master.getPassWord());
        FullNameField.setPromptText(master.getUserFullName());
        DepartmentLabel.setText(Load.loadDepartment(master.getUserDepartmentId()).getDepartmentName());
        PhoneNumberField.setPromptText(master.getUserPhoneNumber());
        NationalNumberField.setPromptText(master.getUserNationalNumber());
        EmailField.setPromptText(master.getUserEmail());
        RoomNumField.setPromptText(master.getMasterRoomNumber());
        MasteryNumberField.setPromptText(master.getMasteryNumber());
        GradeChoice.setValue(master.getMasterGrade()); */
    }

    @Override
    public void update(String input) {

    }
}
