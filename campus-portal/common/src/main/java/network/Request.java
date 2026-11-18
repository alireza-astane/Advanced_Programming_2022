package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Request {
    private String authToken;
    private int requestId;
    private final RequestType requestType;
    private final List<String> args;
    private byte[] file;

    public Request(RequestType requestType, String... args) {
        this.requestType = requestType;
        this.args = new ArrayList<>(Arrays.asList(args));
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public List<String> getArgs() {
        return args == null ? List.of() : args;
    }

    public byte[] getFile() {
        return file;
    }

    public Request withFile(byte[] file) {
        this.file = file;
        return this;
    }
}
