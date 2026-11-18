package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
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

public class Chat extends Gui implements Initializable {
    public Label contactNameLabel;
    public TableColumn message;
    public TableColumn time;
    public TableColumn sender;
    public TableColumn file;
    public Button choosefilebutton;
    public Button sendbutton;
    public TextArea textfield;
    public Label filename;
    public AnchorPane BackGround;

    @Override
    public void update(String input) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));

    }

    public void goMainMenu(ActionEvent actionEvent){
        switch (Client.getInstance().userType){
            case STUDENT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.STUDENT_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
            }
            case ASSISTANT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.ASSISTANT_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.ASSISTANT_MAIN_MENU));
            }

            case MASTER -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.MASTER_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_MAIN_MENU));
            }

            case ADMIN -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.ADMIN_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.ADMIN_MENU_DATA));
            }

            case MOHSENI -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.MOHSENI_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.MOHSENI_MENU_DATA));
            }
        }

    }

    public void goMessengerMenu(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MESSENGER_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MESSENGER_MENU));
    }

    public void goExit(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }
}
