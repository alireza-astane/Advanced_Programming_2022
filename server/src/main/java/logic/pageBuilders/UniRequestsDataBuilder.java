package logic.pageBuilders;

import java.util.ArrayList;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.UniRequest;

public final class UniRequestsDataBuilder {

    private UniRequestsDataBuilder() {
    }

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
}
