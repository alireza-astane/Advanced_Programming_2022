package GuiControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import logic.Configuration;
import logic.enums.FxmlFiles;
import logic.pageDataClasses.CourseListData;
import network.Client;
import network.Request;
import network.RequestType;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class ACoursesListGui extends  Gui implements Initializable {
    CourseListData courseListData;
    public TableColumn numColumn;
    public TableColumn nameColumn;
    public TableColumn masterColumn;
    public TableColumn unitsColumn;
    public TableColumn departmentColumn;
    public TableColumn weeklyplanColumn;
    public TableColumn groupColumn;
    public TableColumn deleteColumn;
    public TableColumn examColumn;
    public ChoiceBox SortChoice;
    public TableColumn editColumn;
    public TableColumn CourseNum;
    public TableColumn CourseName;
    public TableColumn MasterNAme;
    public TableColumn Units;
    public TableColumn department;
    public TableColumn group;
    public TableColumn exam;
    public TableColumn weeklyPlanColumn;

    public void goAddCourse(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        //Gui.goAddCourse(stage);
    }

    public void goEditCourse(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        //Gui.goEditCourse(stage);
    }

    @Override
    public void update(String input) {

    }

    public void setSort(MouseEvent mouseEvent) {
    }

    // public void doRemoveCourse(ActionEvent actionEvent) {
      //  Request.requestRemoveCourse(CourseIdField2.getText());
    //}

    public static class Data{
        public Data(String id,String name,String masterName,String units,String departmentName,String plan,String group,String examTime){
            this.id=id;
            this.name=name;
            this.masterName=masterName;
            this.units=units;
            this.departmentName=departmentName;
            this.plan=plan;
            this.group=group;
            this.examTime=examTime;
        }
        String id;
        String name;
        String masterName;
        String units;
        String departmentName;
        String plan;
        String group;
        String examTime;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public String getPlan() {
            return plan;
        }

        public String getUnits() {
            return units;
        }

        public String getMasterName() {
            return masterName;
        }

        public String getExamTime() {
            return examTime;
        }
        public String getGroup() {
            return group;
        }
    }

    HashMap<String,String> departments = new HashMap<>();
    @FXML
    AnchorPane BackGround;

    @FXML
    TableColumn<Data,String> ColumnC1 = new TableColumn<>("id");

    @FXML
    TableColumn<Data,String> ColumnC2 = new TableColumn<>("name");

    @FXML
    TableColumn<Data,String> ColumnC3 = new TableColumn<>("master's name");

    @FXML
    TableColumn<Data,String> ColumnC4 = new TableColumn<>("units");

    @FXML
    TableColumn<Data,String> ColumnC5 = new TableColumn<>("department");

    @FXML
    TableColumn<Data,String> ColumnC6 = new TableColumn<>("weakly plan");

    @FXML
    TableColumn<Data,String> ColumnC7 = new TableColumn<>("group");

    @FXML
    TableColumn<Data,String> ColumnC8 = new TableColumn<>("examTime");

    @FXML
    Button ExitButton;

    @FXML
    ChoiceBox<Object> DepartmentChoice;

    @FXML
    Button MainMenuButton;

    @FXML
    Label PageLabel;

    @FXML
    Button SMastersListButton;

    @FXML
    Button SCoursesListButton;

    @FXML
    TableView<Data> CoursesList;

    @FXML
    TextField CourseIdField;

    @FXML
    TextField CourseUnitField;

    @FXML
    Button FilterButton;

    @FXML
    Button AddButton;

    @FXML
    TextField CourseIdField2;


    @FXML
    Button RemoveButton;


    public void goACoursesList(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.ASSISTANT_COURSES_LIST);
        Request request = new Request(RequestType.COURSE_LIST_DATA);
        Client.getInstance().getRequester().sendRequest(request);
    }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.goExit(stage);
    }

    public void goMainMenu(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage, FxmlFiles.ASSISTANT_MENU);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.ASSISTANT_MAIN_MENU));
    }

    public void doFilterSCoursesList(ActionEvent actionEvent) {
    //    ArrayList filteredList = Load.loadAllCourses();
     //   for(Object i:Load.loadAllCourses()){
     //       Course course= Load.loadCourse((String) i);
     //       if(!CourseIdField.getText().equals("")&&(!course.getCourseId().contains(CourseIdField.getText()))){filteredList.remove(i);}
     //       if(!CourseUnitField.getText().equals("")&&(!Objects.equals(course.getUnit(), CourseUnitField.getText()))){filteredList.remove(i);}
     //       if((DepartmentChoice.getValue() != null)&&(!Objects.equals(departments.get(DepartmentChoice.getValue().toString()),course.getCourseDepartmentId()))){filteredList.remove(i);}
      //  }
      //  setTable(filteredList);


    }

    public void goAMastersList(ActionEvent actionEvent){
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        Gui.setStage(stage,FxmlFiles.STUDENT_MASTERS_LIST);
        Client.getInstance().getRequester().sendRequest(new Request(RequestType.MASTER_LIST_DATA));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Client.getInstance().controller=this;
        BackGround.setBackground(new Background(new BackgroundFill(Paint.valueOf((String) Configuration.graphics.get("BackGroundColor")),null,null)));

//        departments.put("Physics","1111");
//        departments.put("Math","2222");
//        departments.put("Computer E.","3333");
//        departments.put("Electrical E.","4444");
//        departments.put("Chemistry","5555");
//        DepartmentChoice.setItems(FXCollections.observableArrayList(departments.keySet()));


     //   BackGround.setBackground(new Background(new BackgroundFill(Gui.color,null,null)));
      //  setTable(Load.loadAllCourses());




    }

    private void setTable(ArrayList<Object> courseList) {
        ColumnC1.setCellValueFactory(new PropertyValueFactory<>("id"));
        ColumnC2.setCellValueFactory(new PropertyValueFactory<>("name"));
        ColumnC3.setCellValueFactory(new PropertyValueFactory<>("masterName"));
        ColumnC4.setCellValueFactory(new PropertyValueFactory<>("units"));
        ColumnC5.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        ColumnC6.setCellValueFactory(new PropertyValueFactory<>("plan"));
        ColumnC7.setCellValueFactory(new PropertyValueFactory<>("group"));
        ColumnC8.setCellValueFactory(new PropertyValueFactory<>("examTime"));

        CoursesList.setItems(generateData(courseList));



    }

    private ObservableList<Data> generateData(ArrayList<Object> courseList) {
        int n = courseList.size();
        ObservableList<Data> allData = FXCollections.observableArrayList();
        for (int i = 1; i < n; i++) {

          //  Course course = Load.loadCourse(String.valueOf(courseList.get(i)));

         //   String value1 =course.getCourseId();
         //   String value2 =course.getCourseName();
         //   String value3 =Load.loadUser(course.getCourseMasterId()).getUserFullName();
         //   String value4 =course.getUnit();
         //   String value5 =Load.loadDepartment(course.getCourseDepartmentId()).getDepartmentName();
         //   String value6 =course.getCourseTimeTable().values().toString();
         //   String value7 =course.getGroup();
         //   String value8 =course.getExamTime();
         //   Data data = new Data(value1, value2, value3, value4, value5, value6,value7,value8);
         //   allData.add(data);
        }
        return allData;
    }


    Consumer<Integer> delete = index ->{
        Request request = new Request(RequestType.DELETE_COURSE);
        request.setArrayList(new ArrayList<>(Collections.singletonList(courseListData.getCourseList().get(index).get(0))));
        Client.getInstance().getRequester().sendRequest(request);
    };

    Consumer<Integer> edit = index ->{
      Request request = new Request(RequestType.EDIT_COURSE_DATA);
      request.setArrayList(new ArrayList<>(Collections.singletonList(courseListData.getCourseList().get(index).get(0))));
      Client.getInstance().getRequester().sendRequest(request);

    };

}
