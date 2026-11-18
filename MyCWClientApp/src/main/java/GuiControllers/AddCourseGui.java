package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.ResourceBundle;

public class AddCourseGui extends  Gui implements Initializable {
    public TextField numField;
    public Button applyButton;
    public TextField preField;
    public TextField CoField;
    public TextField TAField;
    @FXML
    Button AddButton;

    @FXML
    AnchorPane BackGround;

    @FXML
    TextField CapacityField;

    @FXML
    Label DepartmentLabel;

    @FXML
    Label ErrorLabel;

    @FXML
    TextField ExamTimeField;

    @FXML
    Button ExitButton;

    @FXML
    TextField GroupField;

    @FXML
    TextField IdField;

    @FXML
    Button MainMenuButton;

    @FXML
    TextField MasterIdField;

    @FXML
    TextField NameField;

    @FXML
    TextField SemesterField;

    @FXML
    TextField TimeTableField;

    @FXML
    TextField UnitField;

    public void doAddCourse(ActionEvent actionEvent) {
        //Request.requestAddCourse(IdField.getText(),NameField.getText(),SemesterField.getText(),ExamTimeField.getText(),UnitField.getText(),GroupField.getText(),CapacityField.getText(),TimeTableField.getText());
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
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
    }

    @Override
    public void update(String input) {

    }

    public void doapplyCourse(ActionEvent actionEvent) {
    }
}
