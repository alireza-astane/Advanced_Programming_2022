package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.MessageType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue
    @Column(name = "messageId")
    int messageId;

    @Column(name = "messageType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    MessageType messageType;

    @Column(name = "messageContext")
    String messageContent="";

    @Column(name = "messageTime")
    LocalDateTime messageTime;

    @Lob
    @Column(name = "messageFile")
    byte[] messageFile;

    @Column(name = "messageChatId")
    int messageChatId;

    @Column(name = "messageSenderId")
    int messageSenderId;



    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(LocalDateTime messageTime) {
        this.messageTime = messageTime;
    }

    public byte[] getMessageFile() {
        return messageFile;
    }

    public void setMessageFile(byte[] messageFile) {
        this.messageFile = messageFile;
    }

    public int getMessageChatId() {
        return messageChatId;
    }

    public void setMessageChatId(int messageChatId) {
        this.messageChatId = messageChatId;
    }

    public int getMessageSenderId() {
        return messageSenderId;
    }

    public void setMessageSenderId(int messageSenderId) {
        this.messageSenderId = messageSenderId;
    }



}
