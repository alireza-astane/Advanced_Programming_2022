package network;
import logic.enums.ResponseType;
import java.io.Serializable;

public class Response implements Serializable {
    ResponseType responseType;
    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

}
