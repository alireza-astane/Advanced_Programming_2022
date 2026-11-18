package GuiControllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

public class StartGui extends Gui{
    public Button startButton;

    public void start(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.LOGIN);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.LOGIN_DATA));
        //Client.getInstance().getRequester().sendRequest(new Request(RequestType.LOGIN_DATA));
    }

    @Override
    public void update(String input) {

    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        GuiControllers.Gui.goExit(stage);
    }
}
