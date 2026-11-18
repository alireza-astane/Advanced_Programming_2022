package GuiControllers;

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

public class SEducationalSituationGui    extends GuiControllers.Gui implements Initializable {
    public static final String Column1MapKey = "A";
    public static final String Column2MapKey = "B";
    public static final String Column3MapKey = "C";
    public TableColumn id;
    public TableColumn name;
    public TableColumn num;
    public TableColumn score;
    @FXML
    AnchorPane BackGround;
    @FXML
    TableView<Object> DataTable;

    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Label MeanScoreLabel;

    @FXML
    Label PassedCoursesLabel;

    @FXML
    TableColumn<Map,String> ColumnC1= new TableColumn<>("Course Name");

    @FXML
    TableColumn<Map,String> ColumnC2= new TableColumn<>("Course Number");

    @FXML
    TableColumn<Map,String> ColumnC3= new TableColumn<>("Score");


    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        GuiControllers.Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        GuiControllers.Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //BackGround.setBackground(new Background(new BackgroundFill(GuiControllers.nnn.Gui.color,null,null)));
        //setTable(GuiControllers.nnn.Gui.user.getStudentCoursesList());

    }

    private void setTable(ArrayList<String> coursesList) {
        //DataTable = new TableView(generateDataInMap(coursesList));
        ColumnC1.setCellValueFactory(new MapValueFactory(Column1MapKey));
        ColumnC2.setCellValueFactory(new MapValueFactory(Column2MapKey));
        ColumnC3.setCellValueFactory(new MapValueFactory(Column3MapKey));

        DataTable.getColumns().setAll();

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
