package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Student;

import java.util.ArrayList;
import java.util.Arrays;

public class AddChatData extends Response {
    ArrayList<ArrayList<String>> arrayLists;

    public static AddChatData getAddChatData(ClientHandler clientHandler) {
        AddChatData addChatData = new AddChatData();
        addChatData.setResponseType(ResponseType.ADD_CHAT_DATA);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        try {
            for (Student student : DataManager.getDepartmentById(clientHandler.getUser().getStudent().getStudentDepartmentId()).getDepartmentStudents()) {
                if (student.getEntryYear() == clientHandler.getUser().getStudent().getEntryYear()) {
                    arrayLists.add(new ArrayList<>(Arrays.asList(String.valueOf(student.getUser().getId()), student.getUser().getUserFullName())));
                }
            }
        } catch (Exception ignored) {
        }
        addChatData.setArrayLists(arrayLists);
        return addChatData;
    }
    //Id name


    public ArrayList<ArrayList<String>> getArrayLists() {
        return arrayLists;
    }

    public void setArrayLists(ArrayList<ArrayList<String>> arrayLists) {
        this.arrayLists = arrayLists;
    }
}
