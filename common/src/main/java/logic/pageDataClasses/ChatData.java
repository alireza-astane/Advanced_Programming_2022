package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatData extends Response {
    public ChatData(){
        this.setResponseType(ResponseType.CHAT_DATA);
    }
    String ContactName;
    Integer ContactId;
    ArrayList<ArrayList<String>> messages;


    //arrayList:  sender type str(context or file) time


    public ArrayList<ArrayList<String>> getMessageIds() {
        return messages;
    }

    public Integer getContactId() {
        return ContactId;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactId(Integer contactId) {
        ContactId = contactId;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public void setMessages(ArrayList<ArrayList<String>> messageIds) {
        this.messages = messageIds;
    }
}
