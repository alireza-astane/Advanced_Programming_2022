package GuiControllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import logic.pageDataClasses.ChangePassData;
import logic.pageDataClasses.LoginData;
import network.Client;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordGui extends Gui implements Initializable {
    static Logger log = LogManager.getLogger(ChangePasswordGui.class);
    public Label result;
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();

    ChangePassData changePassData;

    @FXML
    AnchorPane BackGround;

    @FXML
    Label ErrorLabel;

    @FXML
    Button ExitButton;

    @FXML
    Button ChangePassButton;

    @FXML
    TextField ConfirmPassField;

    @FXML
    TextField CurrentPassField;

    @FXML
    Label DescriptionLabel;

    @FXML
    TextField NewPassField;

    public void doChangePassword(ActionEvent actionEvent){
        Client.getInstance().getRequester().requestChangePass(CurrentPassField.getText(),NewPassField.getText(),ConfirmPassField.getText());

    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
    }

    @Override
    public void update(String input) {
        changePassData = gson.fromJson(input, ChangePassData.class);
        if ("CHANGED".equals(changePassData.getResult())) {
            Stage stage = (Stage) ChangePassButton.getScene().getWindow();
            Gui.setStage(stage, FxmlFiles.LOGIN);
        } else {
            ErrorLabel.setText(changePassData.getResult());
        }


    }
}
