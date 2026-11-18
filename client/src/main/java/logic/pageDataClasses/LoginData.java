package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class LoginData extends Response {
    public LoginData(){
        super();
        this.setResponseType(ResponseType.LOGIN_DATA);
    }
    byte[] captchaImageFile;
    String result;


    public byte[] getCaptchaImageFile() {
        return captchaImageFile;
    }

    public String getResult() {
        return result;
    }

    public void setCaptchaImageFile(byte[] captchaImageFile) {
        this.captchaImageFile = captchaImageFile;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
