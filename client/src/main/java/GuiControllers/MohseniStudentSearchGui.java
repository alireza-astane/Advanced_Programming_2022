package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class MohseniStudentSearchGui extends Gui implements Initializable {
    public AnchorPane BackGround;
    public Button ExitButton;
    public Button MainMenuButton;
    public TableView table;
    public TableColumn infobutton;
    public TableColumn name;
    public TableColumn shipnumber;
    public TableColumn grade;
    public TextField textfeild;

    @Override
    public void update(String input) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));

    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MOHSENI_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MOHSENI_MENU_DATA));
    }

    public void goSearchengine(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MOHSENI_SEARCH_ENGINE);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MOHSENI_STUDENT_SEARCH_DATA));
    }

    public void gomosenimessenger(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MOHSENI_MESSENGER);
    }
}
