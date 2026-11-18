package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WeeklyPlanGui  extends  Gui  implements Initializable {
    public TableView table;
    public TableColumn id;
    public TableColumn name;
    public TableColumn plan;
    @FXML
    AnchorPane BackGround;

    @FXML
    Button ExitButton;

    @FXML
    Button MAinMenuButton;

    @FXML
    Label MondayLabel;

    @FXML
    Label PageLabel;

    @FXML
    Label SaturdayLabel;

    @FXML
    Label SundayLAbel;

    @FXML
    Label ThursdayLabel;

    @FXML
    Label TuesdayLabel;

    @FXML
    Label WednesdayLabel;

    public void goMainMenu(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));

    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));
    }

    @Override
    public void update(String input) {

    }
}
