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

public class MCoursesListGui  extends  Gui implements Initializable {
        @FXML
        AnchorPane BackGround;

        @FXML
        TableColumn<?, ?> ColumnC1;

        @FXML
        TableColumn<?, ?> ColumnC2;

        @FXML
        Button ExitButton;

        @FXML
        MenuButton Filter1;

        @FXML
        MenuButton Filter2;

        @FXML
        MenuButton Filter3;

        @FXML
        Button MainMenuButton;

        @FXML
        Label PageLabel;

        @FXML
        Button SMastersListButton;

        @FXML
        Button SSubjectsLIstButton;

        @FXML
        TableView<?> SubjectsList;

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
        public void goMainMenu(ActionEvent actionEvent){
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.MASTER_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_MAIN_MENU));
        }
}
