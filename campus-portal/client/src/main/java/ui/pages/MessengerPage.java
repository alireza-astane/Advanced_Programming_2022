package ui.pages;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import logic.enums.UserType;
import network.Request;
import network.RequestType;
import network.Response;
import ui.DataTable;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class MessengerPage extends Page {
    private final ListView<Map<String, String>> chats = new ListView<>();
    private final VBox messages = new VBox(8);
    private final ScrollPane messageScroll = new ScrollPane(messages);
    private final Label contactName = new Label("Choose a chat");
    private final HBox contactPhoto = new HBox();
    private final TextField compose = new TextField();
    private final Label attachment = new Label();
    private File chosenFile;
    private String chatId;
    private List<Map<String, String>> shownMessages = List.of();

    MessengerPage(Portal portal) {
        super(portal);
    }

    @Override
    public String title() {
        return "Messenger";
    }

    @Override
    public Node view() {
        chats.setCellFactory(list -> new ChatCell());
        chats.getSelectionModel().selectedItemProperty().addListener((observable, before, row) -> {
            if (row != null && !row.get("id").equals(chatId)) {
                chatId = row.get("id");
                shownMessages = List.of();
                refreshChat();
            }
        });
        chats.setPrefWidth(280);
        contactName.getStyleClass().add("section-title");
        messageScroll.setFitToWidth(true);
        messages.setStyle("-fx-padding: 8;");
        compose.setPromptText("Write a message");
        compose.setOnAction(event -> send());
        HBox.setHgrow(compose, Priority.ALWAYS);
        HBox composer = Ui.row(Ui.button("Attach", this::attach), attachment, compose, Ui.primary("Send", this::send));
        BorderPane room = new BorderPane(messageScroll);
        room.setTop(Ui.row(contactPhoto, contactName));
        room.setBottom(composer);
        room.getStyleClass().add("panel");
        HBox split = new HBox(12, chats, room);
        HBox.setHgrow(room, Priority.ALWAYS);
        return Ui.bodyWith(split, actions());
    }

    private Node actions() {
        HBox row = Ui.row(Ui.button("New message", () -> compose(portal, this::refresh)));
        if (portal.role() != UserType.ADMIN && portal.role() != UserType.REGISTRAR) {
            row.getChildren().add(Ui.button("Request a chat by number", this::requestChat));
        }
        if (portal.role() != UserType.ADMIN) {
            row.getChildren().add(Ui.button("Message the administrator", this::messageAdministrator));
        }
        return row;
    }

    @Override
    public void refresh() {
        portal.load(RequestType.CHATS, response -> {
            List<Map<String, String>> rows = response.table("chats");
            if (!rows.equals(chats.getItems())) {
                chats.setItems(FXCollections.observableArrayList(rows));
                rows.stream().filter(row -> row.get("id").equals(chatId)).findFirst()
                        .ifPresent(row -> chats.getSelectionModel().select(row));
            }
            refreshChat();
        });
    }

    private void refreshChat() {
        if (chatId != null) {
            portal.load(RequestType.CHAT, this::showChat, chatId);
        }
    }

    private void showChat(Response chat) {
        contactName.setText(chat.get("name"));
        contactPhoto.getChildren().setAll(Ui.photo(chat.getFile(), 34));
        List<Map<String, String>> rows = chat.table("messages");
        if (!rows.equals(shownMessages)) {
            shownMessages = rows;
            messages.getChildren().setAll(rows.stream().map(this::bubble).toList());
            messageScroll.layout();
            messageScroll.setVvalue(1);
        }
    }

    private Node bubble(Map<String, String> row) {
        boolean mine = Boolean.parseBoolean(row.get("mine"));
        VBox bubble = new VBox(3);
        bubble.getStyleClass().add("bubble");
        if (mine) {
            bubble.getStyleClass().add("mine");
        }
        if (!row.get("text").isEmpty()) {
            Label text = new Label(row.get("text"));
            text.setWrapText(true);
            bubble.getChildren().add(text);
        }
        if (!row.get("fileName").isEmpty()) {
            Button file = Ui.button(row.get("fileName"), () -> portal.download(RequestType.MESSAGE_FILE, row.get("id")));
            file.getStyleClass().setAll("link-button");
            bubble.getChildren().add(file);
        }
        bubble.getChildren().add(Ui.muted(Ui.time(row.get("time"))));
        HBox line = new HBox(bubble);
        line.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return line;
    }

    private void attach() {
        chosenFile = new FileChooser().showOpenDialog(portal.stage());
        attachment.setText(chosenFile == null ? "" : chosenFile.getName());
    }

    private void send() {
        if (chatId == null) {
            portal.showError("Choose a chat first.");
            return;
        }
        Request request = withFile(new Request(RequestType.SEND_MESSAGE, chatId, compose.getText(), fileName()), chosenFile);
        if (request != null) {
            portal.act(request, "", response -> {
                compose.clear();
                chosenFile = null;
                attachment.setText("");
            });
        }
    }

    private String fileName() {
        return chosenFile == null ? "" : chosenFile.getName();
    }

    private Request withFile(Request request, File file) {
        try {
            return file == null ? request : request.withFile(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            portal.showError("That file could not be read.");
            return null;
        }
    }

    private void requestChat() {
        prompt("Request a chat", "Student or professor number",
                number -> portal.act(RequestType.REQUEST_CHAT, "Your chat request was sent.", number));
    }

    private void messageAdministrator() {
        prompt("Message the administrator", "Your message", portal::messageAdministrator);
    }

    private void prompt(String title, String label, java.util.function.Consumer<String> action) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(label);
        dialog.initOwner(portal.stage());
        dialog.showAndWait().filter(text -> !text.isBlank()).ifPresent(action);
    }

    /** Lets the user pick people, narrowing by department, level and entry year, and message them. */
    static void compose(Portal portal, Runnable after) {
        portal.load(RequestType.CONTACTS, contacts -> showComposer(portal, contacts, after));
    }

    private static void showComposer(Portal portal, Response contacts, Runnable after) {
        DataTable table = new DataTable("Nobody matches.");
        table.column("Name", "name").column("Number", "number").column("Department", "department")
                .column("Entry year", "entryYear").column("Level", row -> Ui.words(row.get("level")));
        table.setRows(contacts.table("contacts"));
        table.view().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.view().setPrefSize(640, 260);
        ChoiceBox<String>[] filters = composerFilters(table, contacts);
        TextArea text = new TextArea();
        text.setPromptText("Message");
        text.setPrefRowCount(3);
        Button all = Ui.button("Select all shown", () -> table.view().getSelectionModel().selectAll());
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New message");
        dialog.initOwner(portal.stage());
        dialog.getDialogPane().getStylesheets().addAll(portal.stage().getScene().getStylesheets());
        dialog.getDialogPane().getStyleClass().addAll(portal.stage().getScene().getRoot().getStyleClass());
        dialog.getDialogPane().setContent(new VBox(8, Ui.row(filters[0], filters[1], filters[2], all), table.view(), text));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.showAndWait().filter(ButtonType.OK::equals).ifPresent(answer -> {
            String ids = table.view().getSelectionModel().getSelectedItems().stream()
                    .map(row -> row.get("id")).collect(Collectors.joining(","));
            portal.act(new Request(RequestType.BROADCAST, ids, text.getText(), ""), "Your message was sent.", sent -> after.run());
        });
    }

    @SuppressWarnings("unchecked")
    private static ChoiceBox<String>[] composerFilters(DataTable table, Response contacts) {
        ChoiceBox<String>[] filters = new ChoiceBox[3];
        Runnable apply = () -> table.setFilter(row -> Choices.matches(filters[0], row.get("department"))
                && Choices.matches(filters[1], Ui.words(row.get("level"))) && Choices.matches(filters[2], row.get("entryYear")));
        String[] keys = {"department", "level", "entryYear"};
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            filters[i] = Choices.filter(apply, contacts.table("contacts").stream()
                    .map(row -> key.equals("level") ? Ui.words(row.get(key)) : row.get(key))
                    .filter(value -> !value.isEmpty()).distinct().sorted().toArray(String[]::new));
        }
        return filters;
    }

    private static final class ChatCell extends ListCell<Map<String, String>> {
        @Override
        protected void updateItem(Map<String, String> row, boolean empty) {
            super.updateItem(row, empty);
            if (empty || row == null) {
                setGraphic(null);
                return;
            }
            Label name = new Label(row.get("name"));
            name.setStyle("-fx-font-weight: bold;");
            Label last = Ui.muted(row.get("lastMessage"));
            last.setWrapText(false);
            setGraphic(new VBox(2, name, last, Ui.muted(Ui.time(row.get("time")))));
        }
    }
}
