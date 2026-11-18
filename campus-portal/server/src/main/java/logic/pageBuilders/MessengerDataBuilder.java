package logic.pageBuilders;

import java.util.*;
import logic.enums.ResponseType;
import logic.pageDataClasses.*;
import network.ClientHandler;
import network.Response;
import serverDataBase.models.Chat;
import serverDataBase.models.User;

public final class MessengerDataBuilder {

    private MessengerDataBuilder() {
    }

    public static MessengerData getMessengerMenuData(ClientHandler clientHandler) {
        MessengerData messengerData = new MessengerData();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        ArrayList<Chat> chats = new ArrayList<>(clientHandler.getUser().getUserChats());
        //sort

        chats.sort(new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
                return o1.getLastMessageTime().compareTo(o2.getLastMessageTime());
            }
        });
    
        for(Chat chat:chats){
            ArrayList<String> arrayList = new ArrayList<>();
            List<User> users = new ArrayList<>(chat.getChatUsers());
            users.remove(clientHandler.getUser());
            arrayList.add(users.get(0).getUserFullName());
            arrayList.add(String.valueOf(chat.getChatId()));
            arrayList.add(chat.getLastMessage());
            arrayList.add(chat.getLastMessageTime().toString());
            arrayLists.add(arrayList);
        }
        messengerData.setChatsArraylist(arrayLists);
        return  messengerData;
    }
}
