package GuiControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import logic.enums.FxmlFiles;

import java.io.IOException;
import java.util.Optional;

public abstract class Gui {
    public abstract void update(String input);

    public static void setStage(Stage stage, FxmlFiles fxml) {
        System.out.println(stage);
        FXMLLoader loader = new FXMLLoader(Gui.class.getClassLoader().getResource(fxml.path));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ignored) {
        }
        assert root != null;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


    }

    public static void goExit(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText("Wanna exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            stage.close();
        }
    }




/*

        FileChooser file = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("images", "*.jpg","*JPG","*png","*PNG");
        file.getExtensionFilters().add(extFilter);
        file.setTitle("Upload Image");
        Stage stage = ((Stage)(((Node)(actionEvent.getTarget())).getScene()).getWindow());
        File file1 = file.showOpenDialog(stage);
        byte[] array = Files.readAllBytes(file.toPath());






            @FXML
    private DateTimePicker datePicker = new DateTimePicker();

 */


    public boolean yesOrNoDialog(String context) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog");
        alert.setHeaderText(null);
        alert.setContentText(context);
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        ButtonType buttonType = result.get();
        if (buttonTypeOne.equals(buttonType)) {
            return true;
        } else if (buttonTypeTwo.equals(buttonType)) {
            return false;
        }
        return yesOrNoDialog(context);
    }


    public void infoDialog(String context) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dialog");
        alert.setHeaderText(null);
        alert.setContentText(context);
        ButtonType buttonTypeOne = new ButtonType("OK");

        alert.getButtonTypes().setAll(buttonTypeOne);

        Optional<ButtonType> result = alert.showAndWait();
        if (buttonTypeOne.equals(result.get())) {
            assert true;
        } else {
            infoDialog(context);
        }
    }


}
