package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import logic.enums.UserType;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.ResourceBundle;

public class RecordGui   extends  Gui  implements Initializable {
    @FXML
    Button EducationalSituationButton;

    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Label PageLabel;

    @FXML
    Button TemporaryScoresButton;

    public void goTemporaryScores(ActionEvent actionEvent) {
        switch (Client.getInstance().userType){
            case STUDENT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.STUDENT_TEMPORARY_SCORES);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_TEMPORARY_SCORE_DATA));
            }
            case ASSISTANT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.ASSISTANT_TEMPORARY_SCORES);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.ASSISTANT_TEMPORARY_SCORE_DATA));
            }

            case MASTER -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.MASTER_TEMPORARY_SCORES);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_TEMPORARY_SCORE_DATA));
            }
        }


    }

    public void goEducationalSituationLIst(ActionEvent actionEvent) {
        switch (Client.getInstance().userType){
            case STUDENT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.STUDENT_EDUCATIONAL_SITUATION);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
            }
            case ASSISTANT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.ASSISTANT_EDUCATIONAL_SITUATION);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.ASSISTANT_MAIN_MENU));
            }
        }

    }

    public void goExit(ActionEvent actionEvent) {
            Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
            Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        switch (Client.getInstance().userType){
            case STUDENT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
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

    @Override
    public void update(String input) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Client.getInstance().userType== UserType.MASTER){
            EducationalSituationButton.setDisable(false);
            EducationalSituationButton.setVisible(false);
        }

    }
}
