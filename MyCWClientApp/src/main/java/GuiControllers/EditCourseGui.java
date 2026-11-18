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
import network.Client;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCourseGui   extends  Gui  implements Initializable {
    @FXML
    Button EditButton;

    @FXML
    AnchorPane BackGround;

    @FXML
    TextField CapacityField;

    @FXML
    Label DepartmentLabel;

    @FXML
    Label ErrorLabel;

    @FXML
    Button LoadButton;

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

    public void doEditCourse(ActionEvent actionEvent) {
        //Request.requestAddCourse(IdField.getText(),NameField.getText(),SemesterField.getText(),ExamTimeField.getText(),UnitField.getText(),GroupField.getText(),CapacityField.getText(),TimeTableField.getText());
    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        //Gui.goMainMenu(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
    }

    public void doLoadCourse(ActionEvent actionEvent) {
        /* Course course = Load.loadCourse(IdField.getText());
        if(Gui.user.getUserDepartmentId().equals(course.getCourseDepartmentId())){
            NameField.setPromptText(course.getCourseName());
            DepartmentLabel.setText(Load.loadDepartment(course.getCourseDepartmentId()).getDepartmentName());
            SemesterField.setPromptText(course.getSemester());
            ExamTimeField.setPromptText(course.getExamTime());
            UnitField.setPromptText(course.getUnit());
            GroupField.setPromptText(course.getGroup());
            CapacityField.setPromptText(course.getCourseTimeTable().toString());
        }*/

    }

    @Override
    public void update(String input) {

    }
}
