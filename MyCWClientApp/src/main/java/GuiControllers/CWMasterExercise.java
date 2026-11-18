package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import logic.pageDataClasses.CWMasterExerciseData;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class CWMasterExercise   extends  Gui  implements Initializable {
    CWMasterExerciseData cwMasterExerciseData;

    public Label text;
    public TableColumn id;
    public TableColumn answerbutton;
    public TableColumn scorebutton;
    public TableColumn score;
    public TableColumn uploadtime;
    public TableColumn name;
    public TableColumn studentshipnum;
    public Label closetime;
    public Label dedline;
    public Label opentime;
    public AnchorPane BackGround;
    public Label title;

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

    public void gomainmenu(ActionEvent actionEvent) {

        switch (Client.getInstance().userType){
            case ASSISTANT -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.ASSISTANT_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.ASSISTANT_MAIN_MENU));
            }

            case MASTER -> {
                Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
                Gui.setStage(stage,FxmlFiles.MASTER_MENU);
                Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_MAIN_MENU));
            }}
    }

    public void gocwmenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.CW_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.CW_MENU_DATA));
    }

    public void showfile(ActionEvent actionEvent) {
    }


    Consumer<Integer> scoreConsumer = index ->{
        Request request = new Request(RequestType.SCORING_ANSWER);
        //request.setArrayList(new ArrayList<>(Collections.singletonList(.get(index).get(0))));
        Client.getInstance().getRequester().sendRequest(request);
    };
}
