package logic.pageBuilders;

import logic.pageDataClasses.LoginData;
import network.Captcha;
import network.ClientHandler;

public final class LoginDataBuilder {

    private LoginDataBuilder() {
    }

    public static LoginData getLoginData(ClientHandler clientHandler) {
        LoginData loginData = new LoginData();
        Captcha captcha = Captcha.generate();
        clientHandler.captchaCode = captcha.code();
        loginData.setCaptchaImageFile(captcha.image());
        return loginData;
    }

    /** Checks the submitted code, then issues a new one so that each code works only once. */
    public static LoginData getLoginData(ClientHandler clientHandler, String submittedCaptcha) {
        boolean correct = submittedCaptcha != null && submittedCaptcha.equals(clientHandler.captchaCode);
        LoginData loginData = getLoginData(clientHandler);
        if (!correct) {
            loginData.setResult("Wrong captcha code");
        }
        return loginData;
    }
}
