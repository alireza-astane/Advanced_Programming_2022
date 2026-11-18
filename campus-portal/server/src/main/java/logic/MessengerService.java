package logic;

import database.DataManager;
import database.models.*;
import logic.enums.MasterType;
import logic.enums.UniRequestType;
import logic.enums.UserType;
import network.Call;
import network.Problem;
import network.Protocol;
import network.RequestType;
import network.Response;
import network.Routes;

import java.time.LocalDateTime;
import java.util.List;

public final class MessengerService {
    private MessengerService() {
    }

    public static void register(Routes routes) {
        routes.on(RequestType.CHATS, Routes.EVERYONE, MessengerService::chats);
        routes.on(RequestType.CHAT, Routes.EVERYONE, MessengerService::chat);
        routes.on(RequestType.MESSAGE_FILE, Routes.EVERYONE, MessengerService::messageFile);
        routes.on(RequestType.SEND_MESSAGE, Routes.EVERYONE, MessengerService::send);
        routes.on(RequestType.CONTACTS, Routes.EVERYONE, MessengerService::contacts);
        routes.on(RequestType.BROADCAST, Routes.EVERYONE, MessengerService::broadcast);
        routes.on(RequestType.REQUEST_CHAT, Routes.UNIVERSITY, MessengerService::requestChat);
        routes.on(RequestType.MESSAGE_ADMIN, Routes.EVERYONE, MessengerService::messageAdmin);
    }

    private static List<Chat> chatsOf(User user) {
        return DataManager.query(Chat.class,
                "select c from Chat c join c.users u where u = :p0 order by c.lastMessageTime desc", user);
    }

    private static User otherUser(Chat chat, User user) {
        return chat.getUsers().stream().filter(member -> member != user).findFirst().orElse(user);
    }

    private static Response chats(Call call) {
        Response response = new Response();
        for (Chat chat : chatsOf(call.user())) {
            response.row("chats").put("id", chat.getId()).put("name", otherUser(chat, call.user()).getFullName())
                    .put("lastMessage", chat.getLastMessage()).put("time", Format.time(chat.getLastMessageTime()));
        }
        return response;
    }

    private static Chat memberChat(Call call, int id) {
        Chat chat = DataManager.get(Chat.class, id);
        if (!chat.getUsers().contains(call.user())) {
            throw new Problem("You are not part of that chat.");
        }
        return chat;
    }

    private static Response chat(Call call) {
        Chat chat = memberChat(call, call.id(0));
        User contact = otherUser(chat, call.user());
        Response response = new Response().put("name", contact.getFullName()).setFile(contact.getPhoto());
        for (Message message : chat.getMessages()) {
            response.row("messages").put("id", message.getId()).put("sender", message.getSender().getFullName())
                    .put("mine", message.getSender() == call.user()).put("text", message.getText())
                    .put("time", Format.time(message.getSentAt())).put("fileName", message.getFileName());
        }
        return response;
    }

    private static Response messageFile(Call call) {
        Message message = DataManager.get(Message.class, call.id(0));
        memberChat(call, message.getChat().getId());
        return new Response().put("fileName", message.getFileName()).setFile(message.getFile());
    }

    static Chat chatBetween(User first, User second) {
        for (Chat chat : chatsOf(first)) {
            if (chat.getUsers().size() == 2 && chat.getUsers().contains(second)) {
                return chat;
            }
        }
        Chat chat = new Chat();
        chat.getUsers().add(first);
        chat.getUsers().add(second);
        DataManager.save(chat);
        return chat;
    }

    static void addMessage(Chat chat, User sender, String text, String fileName, byte[] file) {
        if (text.isBlank() && file == null) {
            throw new Problem("Write a message or attach a file.");
        }
        if (file != null && file.length > Protocol.MAX_FILE_BYTES) {
            throw new Problem("That file is too large.");
        }
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setText(text);
        message.setFileName(file == null ? null : fileName);
        message.setFile(file);
        DataManager.save(message);
        chat.getMessages().add(message);
        chat.setLastMessage(text.isBlank() ? fileName : text);
        chat.setLastMessageTime(LocalDateTime.now());
    }

    private static Response send(Call call) {
        addMessage(memberChat(call, call.id(0)), call.user(), call.text(1), call.text(2), call.file());
        return null;
    }

    private static boolean canContact(User user, Student student) {
        return switch (user.getUserType()) {
            case STUDENT -> student.getDepartment() == user.getStudent().getDepartment()
                    && student.getEntryYear() == user.getStudent().getEntryYear();
            case ASSISTANT -> student.getDepartment() == user.getMaster().getDepartment();
            case MASTER -> user.getMaster().getMasterType() == MasterType.HEAD
                    ? student.getDepartment() == user.getMaster().getDepartment()
                    : student.getSupervisor() == user.getMaster();
            case REGISTRAR, ADMIN -> true;
        };
    }

    private static Response contacts(Call call) {
        Response response = new Response();
        CatalogService.addDepartments(response);
        User user = call.user();
        for (Student student : DataManager.all(Student.class)) {
            if (student.getUser() != user && canContact(user, student)) {
                response.row("contacts").put("id", student.getId()).put("name", student.getUser().getFullName())
                        .put("number", student.getStudentNumber())
                        .put("department", student.getDepartment().getDepartmentType().title)
                        .put("entryYear", student.getEntryYear()).put("level", student.getStudentType());
            }
        }
        if (user.getStudent() != null && user.getStudent().getSupervisor() != null) {
            Master supervisor = user.getStudent().getSupervisor();
            response.row("contacts").put("id", supervisor.getId()).put("name", supervisor.getUser().getFullName())
                    .put("number", supervisor.getMasterNumber())
                    .put("department", supervisor.getDepartment().getDepartmentType().title)
                    .put("entryYear", "").put("level", "Supervisor");
        }
        return response;
    }

    private static Response broadcast(Call call) {
        User user = call.user();
        List<String> ids = call.list(0, ",");
        if (ids.isEmpty()) {
            throw new Problem("Choose at least one person.");
        }
        for (String id : ids) {
            User target = DataManager.get(User.class, Integer.parseInt(id));
            if (!isContact(user, target)) {
                throw new Problem("You cannot message " + target.getFullName() + " directly.");
            }
            addMessage(chatBetween(user, target), user, call.text(1), call.text(2), call.file());
        }
        return null;
    }

    private static boolean isContact(User user, User target) {
        if (target.getStudent() != null && canContact(user, target.getStudent())) {
            return true;
        }
        return user.getStudent() != null && user.getStudent().getSupervisor() == target.getMaster()
                && target.getMaster() != null;
    }

    private static Response requestChat(Call call) {
        String number = call.required(0, "Number");
        User target = findByNumber(number);
        EducationService.create(UniRequestType.CHAT, call.user(), target,
                Configuration.label("chatRequest", call.user().getFullName()));
        return null;
    }

    private static User findByNumber(String number) {
        Student student = DataManager.first(Student.class, "from Student where studentNumber = :p0", number);
        if (student != null) {
            return student.getUser();
        }
        Master master = DataManager.first(Master.class, "from Master where masterNumber = :p0", number);
        if (master == null) {
            throw new Problem("Nobody has the number " + number + ".");
        }
        return master.getUser();
    }

    private static Response messageAdmin(Call call) {
        User admin = DataManager.first(User.class, "from User where userType = :p0 order by id", UserType.ADMIN);
        if (admin == null || admin == call.user()) {
            throw new Problem("There is no administrator to message.");
        }
        addMessage(chatBetween(call.user(), admin), call.user(), call.required(0, "Message"), "", null);
        return null;
    }
}
