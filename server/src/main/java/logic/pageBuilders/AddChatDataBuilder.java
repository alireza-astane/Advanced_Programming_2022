package logic.pageBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import logic.enums.MasterType;
import network.ClientHandler;
import serverDataBase.models.*;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Student;

public final class AddChatDataBuilder {

    private AddChatDataBuilder() {
    }

    public static AddChatData getAddChatData(ClientHandler clientHandler) {
        AddChatData addChatData = new AddChatData();
        addChatData.setResponseType(ResponseType.ADD_CHAT_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        User user = clientHandler.getUser();
        for (Student student : DataManager.getStudents()) {
            if (student.getId() != user.getId() && canContact(user, student)) {
                arrayLists.add(contact(student.getUser(), student.getStudentshipNumber()));
            }
        }
        if (user.getStudent() != null) {
            User supervisor = DataManager.getUserById(user.getStudent().getStudentSupervisorId());
            if (supervisor != null) {
                arrayLists.add(contact(supervisor, supervisor.getMaster().getMasterShipNumber()));
            }
        }

        addChatData.setArrayLists(arrayLists);
        return addChatData;
    }

    private static boolean canContact(User user, Student student) {
        return switch (user.getUserType()) {
            case STUDENT -> student.getStudentDepartmentId() == user.getStudent().getStudentDepartmentId()
                    && student.getEntryYear() == user.getStudent().getEntryYear();
            case ASSISTANT -> student.getStudentDepartmentId() == user.getMaster().getMasterDepartmentId();
            case MASTER -> user.getMaster().getMasterType() == MasterType.BOSS
                    ? student.getStudentDepartmentId() == user.getMaster().getMasterDepartmentId()
                    : student.getStudentSupervisorId() == user.getId();
            case MOHSENI, ADMIN -> true;
        };
    }

    private static ArrayList<String> contact(User user, String number) {
        return new ArrayList<>(Arrays.asList(String.valueOf(user.getId()), user.getUserFullName(), number));
    }
}
