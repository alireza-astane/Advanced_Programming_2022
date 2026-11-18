package logic.enums;

import logic.Configuration;

public enum CaptchaImages {
    A("2412", Configuration.addresses.getProperty("2412")),
    B("2406",Configuration.addresses.getProperty("2406")),
    C("2356",Configuration.addresses.getProperty("2356")),
    D("5044",Configuration.addresses.getProperty("5044")),
    E("8899",Configuration.addresses.getProperty("8899"));


    public final String path;
    public final String code;
    CaptchaImages(String code, String path){
        this.path=path;
        this.code=code;
    }

}
