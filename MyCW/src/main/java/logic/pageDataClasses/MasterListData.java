package logic.pageDataClasses;
import logic.enums.MasterGrade;
import logic.enums.ResponseType;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Master;

import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public class MasterListData extends Response {
    ArrayList<ArrayList<String>> masterList;

    public static MasterListData getMasterListData(String departmentId, String name, String masterGrade) {
        MasterListData masterListData = new MasterListData();
        masterListData.setResponseType(ResponseType.MASTER_LIST_DATA);
        masterListData.setResponseType(ResponseType.MASTER_LIST_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        ArrayList<Master> masters = DataManager.getMasters();


        if(departmentId!=null){
            masters.removeIf(master -> !String.valueOf(master.getMasterDepartmentId()).equals(departmentId));
        }
        if(name!=null){
            masters.removeIf(master -> !master.getUser().getUserFullName().contains(name));
        }
        if(masterGrade!=null){
            masters.removeIf(master -> !Objects.equals(master.getMasterGrade().toString(), masterGrade));
        }
        for(Master master:masters){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(master.getUser().getUserFullName());
            arrayList.add(DataManager.getDepartmentById(master.getMasterDepartmentId()).getDepartmentType().name);
            arrayList.add(master.getMasterGrade().toString());
            arrayList.add(master.getUser().getUserEmail());

            arrayLists.add(arrayList);
        }

        masterListData.setMasterList(arrayLists);
        return masterListData;
    }
    // arraylist : name,department String,grade String,email  filter:name grade department

    public ArrayList<ArrayList<String>> getMasterList() {
        return masterList;
    }

    public void setMasterList(ArrayList<ArrayList<String>> masterList) {
        this.masterList = masterList;
    }
}
