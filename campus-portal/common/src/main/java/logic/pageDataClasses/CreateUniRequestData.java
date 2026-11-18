package logic.pageDataClasses;

import java.util.ArrayList;

public class CreateUniRequestData {
    public ArrayList<ArrayList<String>> getUniRequestList() {
        return uniRequestList;
    }

    public void setUniRequestList(ArrayList<ArrayList<String>> uniRequestList) {
        this.uniRequestList = uniRequestList;
    }

    ArrayList<ArrayList<String>> uniRequestList;
    //ArrayList : Id,type,result
}
