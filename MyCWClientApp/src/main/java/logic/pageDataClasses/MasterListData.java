package logic.pageDataClasses;
import logic.enums.ResponseType;

import network.Response;


import java.util.ArrayList;
import java.util.Objects;


public class MasterListData extends Response {
    ArrayList<ArrayList<String>> masterList;


    // arraylist : name,department String,grade String,email  filter:name grade department

    public ArrayList<ArrayList<String>> getMasterList() {
        return masterList;
    }

    public void setMasterList(ArrayList<ArrayList<String>> masterList) {
        this.masterList = masterList;
    }
}
