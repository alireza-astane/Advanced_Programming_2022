package logic.pageDataClasses;

import logic.enums.ResponseType;
import network.Response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MessengerData extends Response {
    public MessengerData(){super();
        this.setResponseType(ResponseType.MESSENGER_MENU);
    }
    ArrayList<ArrayList<String>> chatsArraylist;


    //ArrayList:   contactName chatId; lastMessage , lastTime


    public ArrayList<ArrayList<String>> getChatsArraylist() {
        return chatsArraylist;
    }

    public void setChatsArraylist(ArrayList<ArrayList<String>> chatsArraylist) {
        this.chatsArraylist = chatsArraylist;
    }
}
