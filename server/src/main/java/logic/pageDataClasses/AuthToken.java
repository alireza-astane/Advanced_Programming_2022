package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

public class AuthToken extends Response {
    int authToken;

    public AuthToken(int authToken) {
        super();
        this.setResponseType(ResponseType.AUTH_TOKEN);
        this.authToken=authToken;
    }

    public int getAuthToken() {
        return authToken;
    }

    public void setAuthToken(int authToken) {
        this.authToken = authToken;
    }
}
