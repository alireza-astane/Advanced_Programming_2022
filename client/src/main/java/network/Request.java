package network;

import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {

    public Request(RequestType requestType){
        this.setRequestType(requestType);
    }

    Integer authToken;
    byte[] fileInByte;
    RequestType requestType;


    ArrayList<String> arrayList;

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public byte[] getFileInByte() {
        return fileInByte;
    }

    public void setFileInByte(byte[] fileInByte) {
        this.fileInByte = fileInByte;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
    public Integer getAuthToken() {
        return authToken;
    }

    public void setAuthToken(Integer authToken) {
        this.authToken = authToken;
    }

}
