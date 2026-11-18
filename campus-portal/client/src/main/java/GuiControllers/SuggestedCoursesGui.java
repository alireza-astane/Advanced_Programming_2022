package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.ResourceBundle;

public class SuggestedCoursesGui   extends  Gui  implements Initializable {
    public TableView siggestedCourses;
    public TableColumn id1;
    public TableColumn name1;
    public TableColumn department1;
    public TableColumn mark1;
    public TableColumn request1;
    public TableColumn selest1;
    public TableView MarkedCourses;
    public TableColumn id2;
    public TableColumn name2;
    public TableColumn department2;
    public TableColumn dismark2;
    public TableColumn request2;
    public TableColumn select2;
    public TableView PreReqigteredCourses;
    public TableColumn id3;
    public TableColumn name3;
    public TableColumn department3;
    public TableColumn mark3;
    public TableColumn deselect3;
    public TableColumn changeGroup3;

    @Override
    public void update(String input) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainmenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));    }

    public void goSuggestedCoursesList(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.SUGGESTED_COURSES_LIST);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.UNIT_SELECTION_DATA));
    }

    public void goourseList(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.UNIT_SELECTION_DATA));
    }
}
