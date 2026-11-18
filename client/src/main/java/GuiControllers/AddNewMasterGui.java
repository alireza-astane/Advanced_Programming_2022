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
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddNewMasterGui extends  Gui implements Initializable {
    HashMap<String,String> departments = new HashMap<>();
    //HashMap<String, MasterGrade> grades = new HashMap<>();

    File file1;
    @FXML
    Button AddButton;

    @FXML
    AnchorPane BackGround;

    @FXML
    ChoiceBox<Object> DepartmentChoice;

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


//        departments.put("Physics","1111");
//        departments.put("Math","2222");
//        departments.put("Computer E.","3333");
//        departments.put("Electrical E.","4444");
//        departments.put("Chemistry","5555");
//        DepartmentChoice.setItems(FXCollections.observableArrayList(departments.keySet()));

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

    public void doAddMaster(ActionEvent actionEvent) {
        /*if(IdField.getText().equals("")||
                PassField.getText().equals("")||
                FullNameField.getText().equals("")||
                (DepartmentChoice.getValue()==null)||
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
                        departments.get(DepartmentChoice.getValue().toString()),NationalNumberField.getText(),
                        RoomNumField.getText(),MasteryNumberField.getText(),
                        grades.get(GradeChoice.getValue().toString()),EmailField.getText(),file1);
            }
        } */
    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.ASSISTANT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.ASSISTANT_MAIN_MENU));
    }

    @Override
    public void update(String input) {

    }
}
