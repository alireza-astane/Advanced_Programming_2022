package generals.gui;

import generals.ai.Computer;
import generals.logic.Game;
import generals.logic.Move;
import generals.logic.PieceType;
import generals.logic.Square;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** Plays a seeded game through the real interface and saves what it shows. */
public final class Screenshots {
    private static final int MIDGAME_PLIES = 17;

    private static File directory;
    private static GameView view;
    private static Scene scene;
    private static int frame;

    public static void main(String[] args) throws Exception {
        directory = new File(args[0]);
        new File(directory, "frames").mkdirs();
        CompletableFuture<Void> done = new CompletableFuture<>();
        Platform.startup(() -> {
            try {
                render();
                done.complete(null);
            } catch (Throwable e) {
                done.completeExceptionally(e);
            }
        });
        done.get(120, TimeUnit.SECONDS);
        Platform.exit();
        System.exit(0);
    }

    private static void render() throws Exception {
        view = new GameView();
        view.setOpponent("Another person");
        scene = GeneralsApp.createScene(view);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        view.board().setRenderScale(2);
        view.applyCss();
        view.layout();

        Random random = new Random(21);
        for (int i = 0; i < MIDGAME_PLIES; i++) {
            playAnimated(Computer.choose(view.current(), Computer.Level.NORMAL, random));
        }
        select();
        save("board.png", 2);
        release();

        while (!view.current().isOver()) {
            view.board().finishSlide();
            view.board().finishSlide();
            playAnimated(Computer.choose(view.current(), Computer.Level.NORMAL, random));
        }
        ImageIO.write(SwingFXUtils.fromFXImage(GeneralsApp.icon(), null), "png", new File(directory, "frames/icon.png"));
    }

    private static void playAnimated(Move move) throws Exception {
        BoardCanvas board = view.board();
        Game before = view.current();
        double startX = move.isDrop() ? board.trayX(move.type()) : board.squareX(move.from());
        double startY = move.isDrop() ? board.trayY(before.turn()) : board.squareY(move.from());
        mouse(MouseEvent.MOUSE_PRESSED, startX, startY);
        mouse(MouseEvent.MOUSE_RELEASED, board.squareX(move.to()), board.squareY(move.to()));
        if (view.moveLines().isEmpty() || view.current() == before) {
            throw new IllegalStateException("move was not accepted: " + move);
        }
        for (double t : new double[]{0.15, 0.4, 0.7}) {
            board.holdSlide(t);
            saveFrame();
        }
        board.finishSlide();
        for (int i = 0; i < 4; i++) {
            saveFrame();
        }
    }

    private static void select() {
        Game game = view.current();
        for (Square square : Game.squares()) {
            var piece = game.pieceAt(square);
            if (piece != null && piece.owner() == game.turn() && piece.type() != PieceType.KING
                    && game.targets(square).size() >= 3) {
                BoardCanvas board = view.board();
                mouse(MouseEvent.MOUSE_PRESSED, board.squareX(square), board.squareY(square));
                return;
            }
        }
    }

    private static void release() {
        mouse(MouseEvent.MOUSE_RELEASED, 0, 0);
    }

    private static void mouse(javafx.event.EventType<MouseEvent> type, double x, double y) {
        Event.fireEvent(view.board(), new MouseEvent(type, x, y, x, y, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, true, null));
    }

    private static void saveFrame() throws Exception {
        write(new File(directory, String.format("frames/%04d.png", frame++)), 1);
    }

    private static void save(String name, double scale) throws Exception {
        write(new File(directory, name), scale);
    }

    private static void write(File file, double scale) throws Exception {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setTransform(new Scale(scale, scale));
        WritableImage image = scene.getRoot().snapshot(parameters, null);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
    }

    private Screenshots() {
    }
}
