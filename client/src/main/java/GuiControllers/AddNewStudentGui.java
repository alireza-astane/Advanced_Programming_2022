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
import logic.enums.StudentState;
import logic.enums.StudentType;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddNewStudentGui extends  Gui implements Initializable {
    public Label departmentLAbel;
    HashMap<String,String> departments = new HashMap<>();
    HashMap<String, StudentState> states = new HashMap<>();
    HashMap<String, StudentType> types = new HashMap<>();

    File file1;

    @FXML
    AnchorPane BackGround;
    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Button UploadImageButton;

    @FXML
    Button AddButton;

    @FXML
    Label ErrorLabel;

    @FXML
    TextField FullNameField;

    @FXML
    TextField IdField;

    @FXML
     TextField NationalNumberField;

    @FXML
     TextField PassField;

    @FXML
     TextField PhoneNumberField;

    @FXML
    ChoiceBox<Object> DepartmentChoice;

    @FXML
    TextField EntryYearField;

    @FXML
    ChoiceBox<Object> StateChoice;

    @FXML
    TextField StudentshipNumberField;

    @FXML
    ChoiceBox<Object> TypeChoice;

    @FXML
    TextField SupervisorIdField;

    @FXML
    TextField EmailField;


    public void doUploadImage(ActionEvent actionEvent){
        FileChooser file = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("images", "*.jpg","*JPG","*png","*PNG");
        file.getExtensionFilters().add(extFilter);
        file.setTitle("Upload Image");
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        file1 = file.showOpenDialog(stage);
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

    public void doAddStudent(ActionEvent actionEvent) { /*
        if(IdField.getText().equals("")||
                PassField.getText().equals("")||
                FullNameField.getText().equals("")||
                (DepartmentChoice.getValue()==null)||
                NationalNumberField.getText().equals("")||
                SupervisorIdField.getText().equals("")||
                EntryYearField.getText().equals("")||
                StudentshipNumberField.getText().equals("")||
                (StateChoice.getValue()==null)||
                (TypeChoice.getValue()==null)||
                EmailField.getText().equals("")||
                (file1==null)){ErrorLabel.setText("Please Fill All Data");}
        else{
            if(Load.loadAllUsers().contains(IdField.getText())){ErrorLabel.setText("This Id Already Exits!");}
            else{
                ErrorLabel.setText("User Creation Was Successful");
                Request.requestAddNewStudent(IdField.getText(),PassField.getText(), FullNameField.getText(),
                        departments.get(DepartmentChoice.getValue().toString()),NationalNumberField.getText(),
                        SupervisorIdField.getText(),EntryYearField.getText(),StudentshipNumberField.getText(),
                        states.get(StateChoice.getValue().toString()),types.get(TypeChoice.getValue().toString()),EmailField.getText(),file1);
            }
        } */
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
        //BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));

        departments.put("Physics","1111");
        departments.put("Math","2222");
        departments.put("Computer E.","3333");
        departments.put("Electrical E.","4444");
        departments.put("Chemistry","5555");
        DepartmentChoice.setItems(FXCollections.observableArrayList(departments.keySet()));


        states.put("Allowed to R.",StudentState.ALLOWED_TO_REGISTER);
        states.put("Edu. Leave",StudentState.EDUCATIONAL_LEAVE);
        states.put("Graduated",StudentState.GRADUATED);
        states.put("Withdrew",StudentState.WITHDREW);
        StateChoice.setItems(FXCollections.observableArrayList(states.keySet()));

        types.put("Undergraduate",StudentType.UNDERGRADUATE);
        types.put("Mastery",StudentType.MASTERY);
        types.put("Phd",StudentType.PHD);
        TypeChoice.setItems(FXCollections.observableArrayList(types.keySet()));
    }

    @Override
    public void update(String input) {

    }
}
