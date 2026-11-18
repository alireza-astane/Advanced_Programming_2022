package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PhdRequestsGui   extends GuiControllers.Gui implements Initializable {
    @FXML
    AnchorPane BackGround;
    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Button DoctoralDissertationDefenseButton;

    @FXML
    Button EnrollmentCertificateButton;

    @FXML
    Button WithdrawalButton;

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        GuiControllers.Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));


    }

    public void goDoctoralDissertationDefense(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        setStage(stage, FxmlFiles.ENROLLMENT_CERTIFICATE);


    }

    public void goEnrollmentCertificate(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        setStage(stage, FxmlFiles.ENROLLMENT_CERTIFICATE);
    }

    public void goWithdrawal(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        setStage(stage, FxmlFiles.WITHDRAWAL);
    }

    @Override
    public void update(String input) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
    }
}
