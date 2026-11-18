package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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

public class MohseniStudentInfoGui extends Gui implements Initializable {
    public AnchorPane BackGround;
    public Button MainMenuButton;
    public Button ExitButton;
    public ImageView UserImage;
    public Label FullNameLabel;
    public Label NationalNumberLabel;
    public Label MeanScoreLabel;
    public Label StudentShipNumber;
    public Label DepartmentLabel;
    public Label SupervisorLabel;
    public Label GradeLabel;
    public Label EntryYearLabel;
    public Label StateLabel;

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.MOHSENI_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MOHSENI_MENU_DATA));
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
