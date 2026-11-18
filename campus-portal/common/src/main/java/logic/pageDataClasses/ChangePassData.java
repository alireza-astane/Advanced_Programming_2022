package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

public class ChangePassData extends Response {
    public ChangePassData(){
        this.setResponseType(ResponseType.CHANGE_PASS_DATA);
    }
    String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
