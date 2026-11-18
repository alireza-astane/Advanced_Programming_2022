package logic.pageBuilders;

import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.EducationalContext;

public final class CWEducationalContextDataBuilder {

    private CWEducationalContextDataBuilder() {
    }

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
}
