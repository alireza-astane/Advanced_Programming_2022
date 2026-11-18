package logic.pageDataClasses;

import logic.enums.ResponseType;


import network.Response;


import java.util.ArrayList;

public class UniRequestsData extends Response {
    ArrayList<ArrayList<String>> uniRequestsList;



    public ArrayList<ArrayList<String>> getUniRequestsList() {
        return uniRequestsList;
    }

    public void setUniRequestsList(ArrayList<ArrayList<String>> uniRequestsList) {
        this.uniRequestsList = uniRequestsList;
    }

    //ArrayList : type requester time result content


}
