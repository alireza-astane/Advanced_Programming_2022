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

public class MTemporaryScoreGui   extends GuiControllers.Gui implements Initializable {
    public TableColumn studentid;
    public TableColumn name;
    public TableColumn score;
    public TableColumn protestation;
    public TableColumn protestationanswer;
    public TableColumn editbutton;
    public TableColumn answerbutton;
    public TextField searchfield;
    @FXML
    AnchorPane BackGround;

    @FXML
    TextField AnswerField;

    @FXML
    TableView<?> DataLabel;

    @FXML
    Button EditButton;

    @FXML
    Button FinalRecordButton;

    @FXML
    Button RecordButton;

    @FXML
    TextField ScoreField;

    @FXML
    Label StudentProtestation;

    @FXML
    MenuButton SubjectMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));

    }

    @Override
    public void update(String input) {

    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MASTER_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_MAIN_MENU));
    }

    public void doSearch(ActionEvent actionEvent) {
    }

    public void doVerify(ActionEvent actionEvent) {
    }
}
