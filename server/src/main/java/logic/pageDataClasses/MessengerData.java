package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.ClientHandler;
import network.Response;
import serverDataBase.models.Chat;
import serverDataBase.models.User;

import java.util.*;

public class MessengerData extends Response {
    public MessengerData(){super();
        this.setResponseType(ResponseType.MESSENGER_MENU);
    }
    ArrayList<ArrayList<String>> chatsArraylist;

    public static MessengerData getMessengerMenuData(ClientHandler clientHandler) {
        MessengerData messengerData = new MessengerData();
        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        ArrayList<Chat> chats = (ArrayList<Chat>) clientHandler.getUser().getUserChats();
        //sort

        try {
            chats.sort(new Comparator<Chat>() {
                @Override
                public int compare(Chat o1, Chat o2) {
                    return o1.getLastMessageTime().compareTo(o2.getLastMessageTime());
                }
            });
        } catch (Exception ignored) {
        }
        for(Chat chat:chats){
            ArrayList<String> arrayList = new ArrayList<>();
            List<User> users = chat.getChatUsers();
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

    //ArrayList:   contactName chatId; lastMessage , lastTime


    public ArrayList<ArrayList<String>> getChatsArraylist() {
        return chatsArraylist;
    }

    public void setChatsArraylist(ArrayList<ArrayList<String>> chatsArraylist) {
        this.chatsArraylist = chatsArraylist;
    }
}
