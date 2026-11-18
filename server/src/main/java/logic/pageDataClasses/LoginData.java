package logic.pageDataClasses;

import logic.enums.CaptchaImages;
import logic.enums.ResponseType;
import network.ClientHandler;
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

    public static LoginData getLoginData(ClientHandler clientHandler) throws IOException {
        LoginData loginData = new LoginData();

        List<CaptchaImages> ImageList = Arrays.asList(CaptchaImages.A,CaptchaImages.B,CaptchaImages.C,CaptchaImages.D,CaptchaImages.E);
        Random rand = new Random();
        CaptchaImages RImage = ImageList.get(rand.nextInt(ImageList.size()));
        clientHandler.captchaCode = RImage.code;
        loginData.setCaptchaImageFile(clientHandler.responser.file2Bytes(RImage.path));

        return loginData;
    }

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
