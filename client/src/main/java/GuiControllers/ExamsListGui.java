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
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import logic.Configuration;
import logic.enums.FxmlFiles;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.*;

public class ExamsListGui   extends  Gui  implements Initializable{
    public static final String Column1MapKey = "A";
    public static final String Column2MapKey = "B";
    public static final String Column3MapKey = "C";
    public Button MainMenuButton;
    public TableColumn id;
    public TableColumn name;
    public TableColumn examTime;

    @FXML
    AnchorPane BackGround;
    @FXML
    private TableColumn<Map,String> ColumnC1= new TableColumn<>("Course id");

    @FXML
    private TableColumn<Map,String> ColumnC2= new TableColumn<>("Course Name");

    @FXML
    private TableColumn<Map,String> ColumnC3= new TableColumn<>("Exam Time");

    @FXML
    Button ExitButton;

    @FXML
    Label PageLabel;

    @FXML
    TableView ExamsList;

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));
        //BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));
        //setTable(Gui.user.getStudentCoursesList());

    }

    private void setTable(ArrayList coursesList) {
        ExamsList = new TableView(generateDataInMap(coursesList));
        ColumnC1.setCellValueFactory(new MapValueFactory(Column1MapKey));
        ColumnC2.setCellValueFactory(new MapValueFactory(Column2MapKey));
        ColumnC3.setCellValueFactory(new MapValueFactory(Column3MapKey));

        ExamsList.getColumns().setAll(ColumnC1,ColumnC2,ColumnC3);

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

    private ObservableList generateDataInMap(ArrayList coursesList) {
        int n = coursesList.size();
        //todo sorting!
        Collections.sort(coursesList);
        ObservableList<Map> allData = FXCollections.observableArrayList();
        for (int i = 1; i < n; i++) {
            Map<String, String> dataRow = new HashMap<>();
            //Course course = Load.loadCourse(String.valueOf(coursesList.get(i)));

            //String value1 =course.getCourseId();
            //String value2 =course.getCourseName();
            //String value3 =course.getExamTime();


            //dataRow.put(Column1MapKey, value1);
            //dataRow.put(Column2MapKey, value2);
           // dataRow.put(Column3MapKey, value3);


            allData.add(dataRow);
        }
        return allData;
    }

    @Override
    public void update(String input) {

    }

    //@Override
    //public int compare(Course o1, Course o2) {
     //   return o1.getExamTime().compareTo(o2.getExamTime());
    //}
}

