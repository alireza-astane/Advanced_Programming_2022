package ui.pages;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import network.Request;
import network.RequestType;
import network.Response;
import ui.Page;
import ui.Portal;
import ui.Ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

final class ProfilePage extends Page {
    private final StackPane photo = new StackPane();
    private final VBox details = new VBox();
    private final TextField email = new TextField();
    private final TextField phone = new TextField();
    private boolean filled;

    ProfilePage(Portal portal) {
        super(portal);
    }

    @Override
    public String title() {
        return "Profile";
    }

    @Override
    public Node view() {
        VBox left = new VBox(8, photo, Ui.button("Change photo", this::choosePhoto));
        HBox summary = new HBox(22, left, details);
        summary.getStyleClass().add("panel");
        email.setPromptText("Email");
        phone.setPromptText("Phone number");
        email.setPrefColumnCount(24);
        HBox contact = Ui.row(email, phone, Ui.primary("Save", this::saveContact));
        return Ui.scrolling(Ui.body(summary, Ui.section("Contact details"), contact));
    }

    private void saveContact() {
        portal.act(RequestType.UPDATE_CONTACT, "Your contact details were saved.", email.getText(), phone.getText());
    }

    private void choosePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(portal.stage());
        if (file != null) {
            try {
                portal.act(new Request(RequestType.UPDATE_PHOTO).withFile(Files.readAllBytes(file.toPath())),
                        "Your photo was updated.", response -> { });
            } catch (IOException e) {
                portal.showError("That file could not be read.");
            }
        }
    }

    @Override
    public void refresh() {
        portal.load(RequestType.PROFILE, this::show);
    }

    private void show(Response profile) {
        photo.getChildren().setAll(Ui.photo(profile.getFile(), 120));
        Ui.Details info = new Ui.Details().add("Name", profile.get("fullName"))
                .add("National number", profile.get("nationalNumber"));
        if (portal.isStudent()) {
            info.add("Student number", profile.get("number")).add("Average", profile.get("average"))
                    .add("Department", profile.get("department")).add("Supervisor", profile.get("supervisor"))
                    .add("Entry year", profile.get("entryYear")).add("Level", Ui.words(profile.get("level")))
                    .add("Status", Ui.words(profile.get("state")));
        } else if (portal.isProfessor()) {
            info.add("Professor number", profile.get("number")).add("Department", profile.get("department"))
                    .add("Room", profile.get("roomNumber")).add("Grade", Ui.words(profile.get("grade")));
        }
        info.add("Phone", profile.get("phone")).add("Email", profile.get("email"));
        details.getChildren().setAll(info.view());
        if (!filled) {
            email.setText(profile.get("email"));
            phone.setText(profile.get("phone"));
            filled = true;
        }
    }
}
