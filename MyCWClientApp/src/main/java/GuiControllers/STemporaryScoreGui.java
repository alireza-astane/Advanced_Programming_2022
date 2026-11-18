package GuiControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class STemporaryScoreGui extends Gui  implements Initializable {
    public static final String Column1MapKey = "A";
    public static final String Column2MapKey = "B";
    public static final String Column3MapKey = "C";
    public static final String Column4MapKey = "D";
    public static final String Column5MapKey = "E";
    public static final String Column6MapKey = "F";
    public TableColumn scoreid;
    public TableColumn coursename;
    public TableColumn score;
    public TableColumn protestationbutton;
    public TableColumn protestation;
    public TableColumn answer;
    @FXML
    AnchorPane BackGround;
    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    private TableColumn<Map, String> ColumnC1 = new TableColumn<>("Course id");

    @FXML
    private TableColumn<Map, String> ColumnC2 = new TableColumn<>("Course Name");

    @FXML
    private TableColumn<Map, String> ColumnC3 = new TableColumn<>("Score");

    @FXML
    private TableColumn<Map, String> ColumnC4 = new TableColumn<>("Pass");

    @FXML
    private TableColumn<Map, String> ColumnC5 = new TableColumn<>("Protestation");

    @FXML
    private TableColumn<Map, String> ColumnC6 = new TableColumn<>("Answer");

    @FXML
    private TextField CourseIdField;

    @FXML
    private TextField ProtestationTextField;

    @FXML
    private TableView TemporaryScores;


    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        GuiControllers.Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        GuiControllers.Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    public void doSubmitProtestation(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setTable(GuiControllers.nnn.Gui.user.getStudentCoursesList());
       // BackGround.setBackground(new Background(new BackgroundFill(GuiControllers.nnn.Gui.color,null,null)));
    }

    private void setTable(ArrayList courseList) {
        //TemporaryScores = new TableView(generateDataInMap(courseList));
        ColumnC1.setCellValueFactory(new MapValueFactory(Column1MapKey));
        ColumnC2.setCellValueFactory(new MapValueFactory(Column2MapKey));
        ColumnC3.setCellValueFactory(new MapValueFactory(Column3MapKey));
        ColumnC4.setCellValueFactory(new MapValueFactory(Column4MapKey));
        ColumnC5.setCellValueFactory(new MapValueFactory(Column5MapKey));
        ColumnC5.setCellValueFactory(new MapValueFactory(Column6MapKey));

        TemporaryScores.getColumns().setAll(ColumnC1, ColumnC2, ColumnC3, ColumnC4, ColumnC5, ColumnC6);

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
        ColumnC4.setCellFactory(cellFactoryForMap);
        ColumnC5.setCellFactory(cellFactoryForMap);
        ColumnC6.setCellFactory(cellFactoryForMap);
    }


    @Override
    public void update(String input) {

    }
}
