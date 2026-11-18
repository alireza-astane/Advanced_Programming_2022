package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue
    @Column(name = "chatId")
    int chatId;


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "chatId")
    private List<Message> messages=new ArrayList<>();

    @ManyToMany(mappedBy = "userChats")
    private List<User> chatUsers = new ArrayList<>();

    @Column(name = "lastMessage")
    String lastMessage;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<User> getChatUsers() {
        return chatUsers;
    }

    public void setChatUsers(List<User> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Column(name = "lastMessageTime")
    LocalDateTime lastMessageTime;

}
