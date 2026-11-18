package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.EducationalContext;

public class CWEducationalContextData extends Response {
    String name;
    String type;
    String text;
    byte[] file;

    public static CWEducationalContextData getCWEducationalContextData(int cwEducationalContextId) {
        CWEducationalContextData cwEducationalContextData = new CWEducationalContextData();
        cwEducationalContextData.setResponseType(ResponseType.CW_EDUCATIONAL_CONTEXT);
        EducationalContext educationalContext = DataManager.getEducationalContextById(cwEducationalContextId);
        cwEducationalContextData.setFile(educationalContext.getEducationalContextFile());
        cwEducationalContextData.setName(educationalContext.getEducationalContextName());
        cwEducationalContextData.setType(educationalContext.getEducationalContextType().toString());
        cwEducationalContextData.setText(educationalContext.getEducationalContextText());
        return cwEducationalContextData;
    }

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
