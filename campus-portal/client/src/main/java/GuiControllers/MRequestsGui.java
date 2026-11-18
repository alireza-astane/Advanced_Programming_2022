package GuiControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import logic.Configuration;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MRequestsGui   extends GuiControllers.Gui  implements Initializable {
    public static final String Column1MapKey = "A";
    public static final String Column2MapKey = "B";
    public static final String Column3MapKey = "C";
    @FXML
    AnchorPane BackGround;
    @FXML
    TableColumn<Map,String> ColumnC1= new TableColumn<>("Request id");

    @FXML
    TableColumn<Map,String> ColumnC2= new TableColumn<>("Request Type");

    @FXML
    TableColumn<Map,String> ColumnC3= new TableColumn<>("Result");
    @FXML
    public TableView RequestsTable;
    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Button WithdrawalButton;

    @FXML
    Button RecommendationButton;

    @FXML
    Button EnrollmentCertificateButton;

    @FXML
    Button DormitoryButton;

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        GuiControllers.Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    public void goRecommendation(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        setStage(stage, FxmlFiles.RECOMMENDATION);

    }

    public void goEnrollmentCertificate(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        setStage(stage, FxmlFiles.ENROLLMENT_CERTIFICATE);
    }

    public void goDormitory(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        setStage(stage, FxmlFiles.DORMITORY);
    }

    public void goWithdrawal(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        setStage(stage, FxmlFiles.WITHDRAWAL);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));

        //setTable(Gui.user.getStudentSentRequestsList());

    }

    private void setTable(ArrayList<String> sentRequestsList) {
        RequestsTable = new TableView();
        ColumnC1.setCellValueFactory(new MapValueFactory(Column1MapKey));
        ColumnC2.setCellValueFactory(new MapValueFactory(Column2MapKey));
        ColumnC3.setCellValueFactory(new MapValueFactory(Column3MapKey));


        RequestsTable.getColumns().setAll(ColumnC1,ColumnC2,ColumnC3);

        Callback<TableColumn<Map, String>, TableCell<Map, String>>
                cellFactoryForMap = (TableColumn<Map, String> p) ->
                new TextFieldTableCell(new StringConverter() {
                    @Override
                    public String toString(Object t) {
                        return t.toString();
                    }
                    @Override
                    public Object fromString(String string) {
                        return string;
                    }
                });
        ColumnC1.setCellFactory(cellFactoryForMap);
        ColumnC2.setCellFactory(cellFactoryForMap);
        ColumnC3.setCellFactory(cellFactoryForMap);

    }



    @Override
    public void update(String input) {

    }
}
