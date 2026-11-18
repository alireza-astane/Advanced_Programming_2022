package logic.pageBuilders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.ClientHandler;
import network.Response;
import serverDataBase.DataManager;
import serverDataBase.models.Chat;
import serverDataBase.models.Message;
import serverDataBase.models.User;

public final class ChatDataBuilder {

    private ChatDataBuilder() {
    }

    public static ChatData getChatData(ClientHandler clientHandler, int chatId) {
        ChatData chatData = new ChatData();
        Chat chat = DataManager.getChatById(chatId);
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        List<User> users = new ArrayList<>(chat.getChatUsers());
        users.remove(clientHandler.getUser());
        chatData.setContactName(users.get(0).getUserFullName());
        chatData.setContactId(users.get(0).getId());


        ArrayList<Message> messages = new ArrayList<>(chat.getMessages());
        messages.sort(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getMessageTime().compareTo(o2.getMessageTime());
            }
        });
        for(Message message: messages){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(String.valueOf(message.getMessageSenderId()));
            arrayList.add(message.getMessageType().toString());
            arrayList.add(message.getMessageTime().toString());
            arrayList.add(message.getMessageContent());
            arrayLists.add(arrayList);
        }
        chatData.setMessages(arrayLists);
    

        return  chatData;



    }
}
