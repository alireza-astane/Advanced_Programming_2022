package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
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

public class AEducationalSituationGui  extends GuiControllers.Gui implements Initializable {
    public TableColumn number;
    @FXML
    AnchorPane BackGround;

    @FXML
    TableView<?> DataTable;

    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Label MeanScoreLabel;

    @FXML
    Label PassedCoursesLabel;

    @FXML
    TableColumn<?, ?> Score;

    @FXML
    Button SearchButton;

    @FXML
    TextField StudentNameField;

    @FXML
    TextField StudentNumField;

    @FXML
   TableColumn<?, ?> SubjectName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
    }

    @Override
    public void update(String input) {

    }

    public void goMainMenu(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.ASSISTANT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.ASSISTANT_MAIN_MENU));
    }

    public void goExit(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }
}
