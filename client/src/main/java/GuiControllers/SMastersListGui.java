package GuiControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;
import logic.enums.MasterGrade;
import network.Client;
import network.Request;
import network.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class SMastersListGui   extends  Gui implements Initializable {
    public TableColumn num;
    public TableColumn name;
    public TableColumn department;
    public TableColumn grade;
    public TableColumn email;

    @Override
    public void update(String input) {

    }

    public static class Data{
        public Data(String id, String name, String departmentName, MasterGrade grade, String email){
            this.id=id;
            this.name=name;
            this.departmentName=departmentName;
            this.grade=grade;
            this.email=email;
        }
        String id;
        String name;
        String departmentName;
        MasterGrade grade;
        String email;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public MasterGrade getGrade() {
            return grade;
        }
        public String getEmail() {
            return email;
        }
    }

    HashMap<String,String> departments = new HashMap<>();
    HashMap<String, MasterGrade> grades = new HashMap<>();

    @FXML
    AnchorPane BackGround;

    @FXML
    TableColumn<Data,String> ColumnC1 = new TableColumn<>("id");

    @FXML
    TableColumn<Data,String> ColumnC2 = new TableColumn<>("master Name");

    @FXML
    TableColumn<Data,String> ColumnC3= new TableColumn<>("department");

    @FXML
    TableColumn<Data,String> ColumnC4= new TableColumn<>("grade");

    @FXML
    TableColumn<Data,String> ColumnC5= new TableColumn<>("email");

    @FXML
    Button ExitButton;

    @FXML
    Button MainMenuButton;

    @FXML
    Label PageLabel;

    @FXML
    Button SMastersListButton;

    @FXML
    Button SCoursesListButton;

    @FXML
    TableView<Data> MastersList;

    @FXML
    ChoiceBox<Object> DepartmentChoice;

    @FXML
    Button FilterButton;


    @FXML
    TextField MasterName;

    @FXML
    ChoiceBox<Object> GradeChoice;

    public void goSCoursesList(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.STUDENT_COURSES_LIST);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.COURSE_LIST_DATA));
    }

    public void goMastersList(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.STUDENT_MASTERS_LIST);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_LIST_DATA));
    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent) throws IOException {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.STUDENT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.STUDENT_MAIN_MENU));
    }

    public void doFilterMastersList(ActionEvent actionEvent) {
     //   ArrayList filteredList = Load.loadAllMasters();
      //  for(Object i:Load.loadAllMasters()){
      //      User user= Load.loadUser((String) i);
       //     if(!MasterName.getText().equals("")&&(!user.getUserFullName().contains(MasterName.getText()))){filteredList.remove(i);}
       //     if((DepartmentChoice.getValue() != null)&&(!Objects.equals(departments.get(DepartmentChoice.getValue().toString()),user.getUserDepartmentId()))){filteredList.remove(i);}
       //     if((GradeChoice.getValue() != null)&&(!Objects.equals(grades.get(GradeChoice.getValue().toString()),user.getMasterGrade()))){filteredList.remove(i);}

      //  }
      //  setTable(filteredList);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
      //  BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));

        departments.put("Physics","1111");
        departments.put("Math","2222");
        departments.put("Computer E.","3333");
        departments.put("Electrical E.","4444");
        departments.put("Chemistry","5555");
        DepartmentChoice.setItems(FXCollections.observableArrayList(departments.keySet()));

        grades.put("Assistant Prof.", MasterGrade.ASSISTANT_PROFESSOR);
        grades.put("Associate Prof.",MasterGrade.ASSOCIATE_PROFESSOR);
        grades.put("Post Doc.",MasterGrade.POSTDOC);
        grades.put("Prof.",MasterGrade.PROFESSOR);
        GradeChoice.setItems(FXCollections.observableArrayList(grades.keySet()));

     //   setTable(Load.loadAllMasters());

    }

    private void setTable(ArrayList mastersList) {
        ColumnC1.setCellValueFactory(new PropertyValueFactory<>("id"));
        ColumnC2.setCellValueFactory(new PropertyValueFactory<>("name"));
        ColumnC3.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        ColumnC4.setCellValueFactory(new PropertyValueFactory<>("grade"));
        ColumnC5.setCellValueFactory(new PropertyValueFactory<>("email"));

        MastersList.setItems(generateData(mastersList));

    }

    private ObservableList generateData(ArrayList mastersList) {
        int n = mastersList.size();
        ObservableList<Data> allData = FXCollections.observableArrayList();
        for (int i = 1; i < n; i++) {

          //  User user = Load.loadUser(String.valueOf(mastersList.get(i)));

         //   String value1 =user.getUserId();
         //   String value2 =user.getUserFullName();
         //   String value3 =user.getUserDepartmentId();
         //   MasterGrade value4 =user.getMasterGrade();
         //   String value5 =user.getUserEmail();

         //   Data data = new Data(value1, value2, value3, value4, value5);
         //   allData.add(data);
        }
        return allData;

    }
}

