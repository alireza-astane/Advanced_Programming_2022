package GuiControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class TableManager {
    private final ObservableList<Data> tvObservableList = FXCollections.observableArrayList();
    TableView<Data> tableView;
    public TableManager(TableView<Data> tableView){
        this.tableView=tableView;
    }



    public void setColumns(ArrayList<TableColumn<Data,String>> columns){
        for(int i=1;i<columns.size()+1;i++){
            columns.get(i).setCellValueFactory(new PropertyValueFactory<>("str"+i));
        }
        tableView.getColumns().addAll(columns);
    }

    public  void setData(ArrayList<ArrayList<String>> datas){
        for(ArrayList<String> arrayList:datas){
            Data data = new Data(arrayList);
            tvObservableList.add(data);
        }
        tableView.setItems(tvObservableList);
    }

    public void addButton(TableColumn<Data, Void> colBtn,Consumer<Integer> consumer,String buttonName){

        Callback<TableColumn<Data, Void>, TableCell<Data, Void>> cellFactory = new Callback<TableColumn<Data, Void>, TableCell<Data, Void>>() {
            @Override
            public TableCell<Data, Void> call(final TableColumn<Data, Void> param) {
                return new TableCell<Data, Void>() {

                    private final Button btn = new Button(buttonName);

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Data data = getTableView().getItems().get(getIndex());
                            //System.out.println("selectedData: " + data);
                            consumer.accept(getIndex());

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);   //?
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        colBtn.setCellFactory(cellFactory);

        tableView.getColumns().add(colBtn);














    }



    public class Data{
        public Data(ArrayList<String> arrayList){
            try{
                this.str1=arrayList.get(0);
            } catch (Exception ignored) {}
            try{
                this.str2=arrayList.get(1);
            } catch (Exception ignored) {}
            try{
                this.str3=arrayList.get(2);
            } catch (Exception ignored) {}
            try{
                this.str4=arrayList.get(3);
            } catch (Exception ignored) {}
            try{
                this.str5=arrayList.get(4);
            } catch (Exception ignored) {}
            try{
                this.str6=arrayList.get(5);
            } catch (Exception ignored) {}
            try{
                this.str7=arrayList.get(6);
            } catch (Exception ignored) {}
            try{
                this.str8=arrayList.get(7);
            } catch (Exception ignored) {}
            try{
                this.str9=arrayList.get(8);
            } catch (Exception ignored) {}
            try{
                this.str10=arrayList.get(9);
            } catch (Exception ignored) {}


        }
        private String str1;
        private String str2;
        private String str3;
        private String str4;
        private String str5;
        private String str6;
        private String str7;
        private String str8;
        private String str9;
        private String str10;
        //you can add more


    }

}
