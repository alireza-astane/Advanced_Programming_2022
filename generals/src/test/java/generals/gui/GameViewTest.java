package generals.gui;

import generals.ai.Computer;
import generals.logic.Game;
import generals.logic.Piece;
import generals.logic.PieceType;
import generals.logic.Side;
import generals.logic.Square;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {

    @BeforeAll
    static void startToolkit() {
        try {
            Platform.startup(() -> { });
        } catch (IllegalStateException alreadyStarted) {
            // another test class started it
        }
        Platform.setImplicitExit(false);
    }

    private static <T> T fx(Callable<T> action) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                result.complete(action.call());
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });
        return result.get(10, TimeUnit.SECONDS);
    }

    private static GameView open(Object opponent) throws Exception {
        return fx(() -> {
            GameView view = new GameView();
            view.setOpponent(opponent);
            Stage stage = new Stage();
            Scene scene = GeneralsApp.createScene(view);
            stage.setScene(scene);
            stage.show();
            view.applyCss();
            view.layout();
            return view;
        });
    }

    private static void mouse(BoardCanvas board, javafx.event.EventType<MouseEvent> type, double x, double y) {
        Event.fireEvent(board, new MouseEvent(type, x, y, x, y, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, true, null));
    }

    private static void click(GameView view, Square square) {
        BoardCanvas board = view.board();
        mouse(board, MouseEvent.MOUSE_PRESSED, board.squareX(square), board.squareY(square));
        mouse(board, MouseEvent.MOUSE_RELEASED, board.squareX(square), board.squareY(square));
    }

    private static void drag(GameView view, Square from, Square to) {
        BoardCanvas board = view.board();
        mouse(board, MouseEvent.MOUSE_PRESSED, board.squareX(from), board.squareY(from));
        mouse(board, MouseEvent.MOUSE_DRAGGED, board.squareX(to), board.squareY(to));
        mouse(board, MouseEvent.MOUSE_RELEASED, board.squareX(to), board.squareY(to));
    }

    @Test
    void clickingAPieceThenASquarePlaysTheMove() throws Exception {
        GameView view = open("Another person");
        fx(() -> {
            click(view, new Square(5, 4));
            click(view, new Square(5, 3));
            assertEquals(new Piece(PieceType.PAWN, Side.BLACK, false), view.current().pieceAt(new Square(5, 3)));
            assertEquals(Side.WHITE, view.current().turn());
            assertEquals("White to move", view.statusText());
            assertEquals(" 1. p 54-53", view.moveLines().get(0));
            return null;
        });
    }

    @Test
    void draggingPlaysAndIllegalDropsAreIgnored() throws Exception {
        GameView view = open("Another person");
        fx(() -> {
            drag(view, new Square(5, 4), new Square(1, 1));
            assertEquals(Side.BLACK, view.current().turn());
            drag(view, new Square(5, 4), new Square(5, 3));
            assertEquals(Side.WHITE, view.current().turn());
            return null;
        });
    }

    @Test
    void capturedPieceCanBeDroppedFromTheTray() throws Exception {
        GameView view = open("Another person");
        fx(() -> {
            drag(view, new Square(2, 5), new Square(5, 2));
            assertEquals(Side.WHITE, view.current().turn(), view.moveLines().toString());
            drag(view, new Square(4, 1), new Square(5, 2));
            assertEquals(java.util.List.of(PieceType.BISHOP), view.current().hand(Side.WHITE));

            drag(view, new Square(5, 4), new Square(5, 3));
            BoardCanvas board = view.board();
            double x = board.trayX(PieceType.BISHOP);
            double y = board.trayY(Side.WHITE);
            Square to = new Square(3, 3);
            mouse(board, MouseEvent.MOUSE_PRESSED, x, y);
            mouse(board, MouseEvent.MOUSE_DRAGGED, board.squareX(to), board.squareY(to));
            mouse(board, MouseEvent.MOUSE_RELEASED, board.squareX(to), board.squareY(to));
            assertEquals(new Piece(PieceType.BISHOP, Side.WHITE, false), view.current().pieceAt(to));
            assertTrue(view.current().hand(Side.WHITE).isEmpty());

            view.undo();
            assertEquals(java.util.List.of(PieceType.BISHOP), view.current().hand(Side.WHITE));
            return null;
        });
    }

    @Test
    void computerRepliesAndUndoReturnsToThePlayersTurn() throws Exception {
        GameView view = open(Computer.Level.EASY);
        fx(() -> {
            drag(view, new Square(5, 4), new Square(5, 3));
            view.board().finishSlide();
            assertTrue(view.isThinking());
            return null;
        });
        long deadline = System.currentTimeMillis() + 5000;
        while (fx(() -> view.moveLines().size()) < 2 && System.currentTimeMillis() < deadline) {
            Thread.sleep(25);
        }
        fx(() -> {
            assertEquals(2, view.moveLines().size());
            view.board().finishSlide();
            view.undo();
            assertTrue(view.moveLines().isEmpty());
            assertEquals(Side.BLACK, view.current().turn());
            assertFalse(view.isThinking());
            return null;
        });
    }

    @Test
    void endOfGameIsAnnouncedAndBlocksInput() throws Exception {
        GameView view = open("Another person");
        fx(() -> {
            // Black's lance walks up the first column and takes the pawn, then the king.
            drag(view, new Square(1, 5), new Square(1, 2));
            drag(view, new Square(1, 1), new Square(2, 2));
            drag(view, new Square(5, 5), new Square(5, 5));
            Game game = view.current();
            assertFalse(game.isOver());
            drag(view, new Square(1, 2), new Square(2, 2));
            assertEquals(Side.BLACK, view.current().winner());
            assertEquals("Black wins", view.statusText());
            int played = view.moveLines().size();
            drag(view, new Square(5, 4), new Square(5, 3));
            assertEquals(played, view.moveLines().size());
            return null;
        });
    }
}
