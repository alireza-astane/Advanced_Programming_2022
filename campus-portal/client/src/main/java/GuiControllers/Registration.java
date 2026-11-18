package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.ResourceBundle;

public class Registration  extends  Gui  implements Initializable {
    @FXML
    AnchorPane BackGround;
    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Label PageLabel;

    @FXML
    Button MastersListButton;

    @FXML
    Button CoursesListButton;


    public void goExit(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
     Gui.goExit(stage);

    }

    public void goMainMenu(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    public void goSCoursesList(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.STUDENT_COURSES_LIST);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.COURSE_LIST_DATA));
    }

    public void goSMastersList(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.STUDENT_MASTERS_LIST);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_LIST_DATA));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     //   BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));
    }

    @Override
    public void update(String input) {

    }
}
