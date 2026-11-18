package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

public class CWEducationalContextData extends Response {
    String name;
    String type;
    String text;
    byte[] file;



    public void setName(String name) {
        this.name = name;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public byte[] getFile() {
        return file;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }
}
