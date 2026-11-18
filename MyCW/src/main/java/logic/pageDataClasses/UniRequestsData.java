package logic.pageDataClasses;

import logic.enums.ResponseType;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.UniRequest;

import java.util.ArrayList;
@Getter
@Setter
public class UniRequestsData extends Response {
    ArrayList<ArrayList<String>> uniRequestsList;

    public static UniRequestsData getUniRequestData(ClientHandler clientHandler) {
        UniRequestsData uniRequestsData = new UniRequestsData();
        uniRequestsData.setResponseType(ResponseType.UNIREQUESTS_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        for(UniRequest uniRequest:clientHandler.getUser().getInUniRequests()){
            ArrayList<String> arrayList =new ArrayList<>();
            arrayList.add(String.valueOf(uniRequest.getUniRequestId()));
            arrayList.add(uniRequest.getUniRequestType().toString());
            arrayList.add(DataManager.getUserById(uniRequest.getUniRequesterId()).getUserFullName());
            arrayList.add(uniRequest.getUniRequestTime().toString());
            arrayList.add(uniRequest.getUniRequestResult().toString());
            arrayList.add(uniRequest.getUniRequestContext());

            arrayLists.add(arrayList);
        }
        uniRequestsData.setUniRequestsList(arrayLists);
        return uniRequestsData;
    }

    public ArrayList<ArrayList<String>> getUniRequestsList() {
        return uniRequestsList;
    }

    public void setUniRequestsList(ArrayList<ArrayList<String>> uniRequestsList) {
        this.uniRequestsList = uniRequestsList;
    }

    //ArrayList : type requester time result content


}
