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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WithdrawalGui extends  Gui implements Initializable {
    @FXML
    AnchorPane BackGround;

    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Label ResultLabel;

    @FXML
    Button RequestButton;

    public void doWithdrawalRequestButton(ActionEvent actionEvent) {
    }

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
