package logic.pageBuilders;

import logic.enums.MasterGrade;
import logic.enums.MasterType;
import logic.enums.ResponseType;
import logic.enums.UserType;
import logic.pageDataClasses.*;
import lombok.Getter;
import lombok.Setter;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Master;

public final class EditMasterDataBuilder {

    private EditMasterDataBuilder() {
    }

    public static EditMasterData getEditMasterData(Integer masterId) {
        EditMasterData editMasterData = new EditMasterData();
        editMasterData.setResponseType(ResponseType.EDIT_MASTER_DATA);
        Master master = DataManager.getUserById(masterId).getMaster();

        editMasterData.setMasterDepartmentId(master.getMasterDepartmentId());
        editMasterData.setMasterType(master.getMasterType());
        editMasterData.setMasterGrade(master.getMasterGrade());
        editMasterData.setMasterRoomNumber(master.getMasterRoomNumber());
        editMasterData.setMasteryNumber(master.getMasterShipNumber());
        editMasterData.setUserEmail(master.getUser().getUserEmail());
        editMasterData.setUsername(master.getUser().getUsername());
        editMasterData.setUserFullName(master.getUser().getUserFullName());
        return editMasterData;
    }
}
