package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Chat;
import serverDataBase.models.Message;
import serverDataBase.models.User;

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

    public static ChatData getChatData(ClientHandler clientHandler, int chatId) {
        ChatData chatData = new ChatData();
        Chat chat = DataManager.getChatById(chatId);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        try{
            List<User> users = chat.getChatUsers();
            users.remove(clientHandler.getUser());
            chatData.setContactName(users.get(0).getUserFullName());
            chatData.setContactId(users.get(0).getId());


            ArrayList<Message> messages = (ArrayList<Message>) chat.getMessages();
            messages.sort(new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    return o1.getMessageTime().compareTo(o2.getMessageTime());
                }
            });
            for(Message message: chat.getMessages()){
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(String.valueOf(message.getMessageSenderId()));
                arrayList.add(message.getMessageType().toString());
                arrayList.add(message.getMessageTime().toString());
                arrayLists.add(arrayList);
            }
            chatData.setMessages(arrayLists);
        } catch (Exception ignored) {
        }

        return  chatData;



    }
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
