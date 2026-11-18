package network;

import logic.enums.RequestType;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Request implements Serializable {
    String authToken;
    byte[] fileInByte;

    ArrayList<String> arrayList;

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList=arrayList;
    }

    RequestType requestType;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public byte[] getFileInByte() {
        return fileInByte;
    }

    public void setFileInByte(byte[] fileInByte) {
        this.fileInByte = fileInByte;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
