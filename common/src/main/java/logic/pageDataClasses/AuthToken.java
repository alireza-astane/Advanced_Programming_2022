package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

public class AuthToken extends Response {
    String authToken;

    public AuthToken(String authToken) {
        super();
        this.setResponseType(ResponseType.AUTH_TOKEN);
        this.authToken=authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
