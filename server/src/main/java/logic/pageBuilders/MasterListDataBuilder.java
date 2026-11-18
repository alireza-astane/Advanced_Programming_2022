package logic.pageBuilders;

import java.util.ArrayList;
import java.util.Objects;
import logic.enums.MasterGrade;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Master;

public final class MasterListDataBuilder {

    private MasterListDataBuilder() {
    }

    public static MasterListData getMasterListData(String departmentId, String name, String masterGrade) {
        MasterListData masterListData = new MasterListData();
        masterListData.setResponseType(ResponseType.MASTER_LIST_DATA);
        masterListData.setResponseType(ResponseType.MASTER_LIST_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        ArrayList<Master> masters = new ArrayList<>(DataManager.getMasters());


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
}
