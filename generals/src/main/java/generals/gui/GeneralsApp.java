package generals.gui;

import generals.logic.Piece;
import generals.logic.PieceType;
import generals.logic.Side;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GeneralsApp extends Application {

    @Override
    public void start(Stage stage) {
        GameView view = new GameView();
        Scene scene = createScene(view);
        stage.setTitle("Generals");
        stage.getIcons().add(icon());
        stage.setMinWidth(760);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    static Scene createScene(GameView view) {
        Scene scene = new Scene(view, 1000, 760);
        scene.getStylesheets().add(GeneralsApp.class.getResource("/generals/generals.css").toExternalForm());
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN), view::undo);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), view::newGame);
        return scene;
    }

    static Image icon() {
        Canvas canvas = new Canvas(256, 256);
        PieceArt.draw(canvas.getGraphicsContext2D(), new Piece(PieceType.KING, Side.WHITE, false), 128, 128, 262);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        return canvas.snapshot(parameters, null);
    }
}
