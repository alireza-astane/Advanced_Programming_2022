package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.Configuration;
import network.Client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MinorGui   extends GuiControllers.Gui  implements Initializable {
    @FXML
    AnchorPane BackGround;
    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Button RequestButton;

    @FXML
    Label ResultLabel;

    @FXML
    MenuButton SubjectMenu;

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }
    public void goMainMEnu(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        //GuiControllers.nnn.Gui.goMainMenu(stage);
    }
    public void DoRequestMinor(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));

    }

    @Override
    public void update(String input) {

    }
}
