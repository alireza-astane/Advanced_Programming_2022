package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

public class ErrorData extends Response {
    String message;

    public ErrorData(String message) {
        this.setResponseType(ResponseType.ERROR);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
