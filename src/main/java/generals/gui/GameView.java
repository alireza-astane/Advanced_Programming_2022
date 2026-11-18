package generals.gui;

import generals.ai.Computer;
import generals.gui.BoardCanvas.Spot;
import generals.logic.Game;
import generals.logic.Move;
import generals.logic.Piece;
import generals.logic.Side;
import generals.logic.Square;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class GameView extends BorderPane implements BoardCanvas.Listener {
    private static final String TWO_PLAYERS = "Another person";
    private static final long MINIMUM_THINKING_MILLIS = 350;
    private static final String RULES = """
            Black moves first. Capture the enemy king to win.

            A captured piece joins your hand. On a later turn you can drop it on any empty square instead of moving.

            Pieces with dots or lines upgrade when they end a move in the two rows nearest the enemy, and their marks turn red. Kings and golden generals never upgrade. Dropped pieces start plain.

            A red frame means that king can be captured next move.
            """;

    private final BoardCanvas board = new BoardCanvas(this);
    private final Label status = new Label();
    private final Label detail = new Label();
    private final ChoiceBox<Object> opponent = new ChoiceBox<>();
    private final ChoiceBox<Side> humanSide = new ChoiceBox<>();
    private final Button undo = new Button("Undo");
    private final ObservableList<String> moveLines = FXCollections.observableArrayList();
    private final ListView<String> moveList = new ListView<>(moveLines);
    private final Random random = new Random();

    private final List<Game> history = new ArrayList<>();
    private final List<Move> moves = new ArrayList<>();
    private Spot selected;
    private List<Square> targets = List.of();
    private boolean dragging;
    private boolean pressPlayed;
    private boolean thinking;
    private int searchToken;

    GameView() {
        getStyleClass().add("game-view");
        setCenter(board);
        BorderPane.setMargin(board, new Insets(14, 8, 14, 14));
        setRight(createSidePanel());
        newGame();
    }

    private VBox createSidePanel() {
        Label title = new Label("Generals");
        title.getStyleClass().add("title");
        status.getStyleClass().add("status");
        detail.getStyleClass().add("detail");
        detail.setWrapText(true);

        opponent.getItems().add(TWO_PLAYERS);
        opponent.getItems().addAll((Object[]) Computer.Level.values());
        opponent.setConverter(new StringConverter<>() {
            @Override
            public String toString(Object value) {
                return value instanceof Computer.Level level ? "Computer, " + level.title.toLowerCase() : String.valueOf(value);
            }

            @Override
            public Object fromString(String text) {
                return null;
            }
        });
        opponent.setValue(Computer.Level.NORMAL);
        opponent.setMaxWidth(Double.MAX_VALUE);
        opponent.setOnAction(event -> settingsChanged());

        humanSide.getItems().addAll(Side.BLACK, Side.WHITE);
        humanSide.setConverter(new StringConverter<>() {
            @Override
            public String toString(Side side) {
                return side == null ? "" : side.title();
            }

            @Override
            public Side fromString(String text) {
                return null;
            }
        });
        humanSide.setValue(Side.BLACK);
        humanSide.setMaxWidth(Double.MAX_VALUE);
        humanSide.setOnAction(event -> settingsChanged());
        humanSide.disableProperty().bind(opponent.valueProperty().isEqualTo((Object) TWO_PLAYERS));

        Button newGame = new Button("New game");
        newGame.setDefaultButton(false);
        newGame.setOnAction(event -> newGame());
        undo.setOnAction(event -> undo());
        newGame.setMaxWidth(Double.MAX_VALUE);
        undo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(newGame, Priority.ALWAYS);
        HBox.setHgrow(undo, Priority.ALWAYS);
        HBox buttons = new HBox(8, newGame, undo);

        Label movesLabel = caption("Moves");
        moveList.setFocusTraversable(false);
        moveList.setPlaceholder(new Label("No moves yet"));
        VBox.setVgrow(moveList, Priority.ALWAYS);

        Label rulesText = new Label(RULES.strip());
        rulesText.setWrapText(true);
        rulesText.getStyleClass().add("rules");
        TitledPane rules = new TitledPane("How to play", rulesText);
        rules.setExpanded(false);

        VBox panel = new VBox(6, title, status, detail, spaced(caption("Opponent")), opponent,
                caption("You play"), humanSide, spaced(buttons), spaced(movesLabel), moveList, rules);
        panel.getStyleClass().add("side-panel");
        panel.setPrefWidth(250);
        panel.setMinWidth(230);
        return panel;
    }

    private static Label caption(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("caption");
        return label;
    }

    private static <T extends javafx.scene.Node> T spaced(T node) {
        VBox.setMargin(node, new Insets(10, 0, 0, 0));
        return node;
    }

    void newGame() {
        cancelSearch();
        board.finishSlide();
        history.clear();
        moves.clear();
        moveLines.clear();
        history.add(new Game());
        clearSelection();
        refresh();
        computerMoveIfDue();
    }

    void undo() {
        if (moves.isEmpty()) {
            return;
        }
        cancelSearch();
        board.finishSlide();
        removeLastMove();
        Side computer = computerSide();
        while (computer != null && !moves.isEmpty() && current().turn() == computer) {
            removeLastMove();
        }
        clearSelection();
        refresh();
        computerMoveIfDue();
    }

    private void removeLastMove() {
        history.remove(history.size() - 1);
        moves.remove(moves.size() - 1);
        moveLines.remove(moveLines.size() - 1);
    }

    private void settingsChanged() {
        cancelSearch();
        clearSelection();
        refresh();
        computerMoveIfDue();
    }

    Game current() {
        return history.get(history.size() - 1);
    }

    BoardCanvas board() {
        return board;
    }

    boolean isThinking() {
        return thinking;
    }

    List<String> moveLines() {
        return List.copyOf(moveLines);
    }

    String statusText() {
        return status.getText();
    }

    void setOpponent(Object value) {
        opponent.setValue(value);
    }

    private Computer.Level level() {
        return opponent.getValue() instanceof Computer.Level level ? level : null;
    }

    private Side computerSide() {
        return level() == null ? null : humanSide.getValue().opponent();
    }

    private boolean humanCanMove() {
        return !current().isOver() && !thinking && current().turn() != computerSide();
    }

    @Override
    public void pressed(Spot spot) {
        pressPlayed = false;
        if (!humanCanMove()) {
            return;
        }
        board.finishSlide();
        if (!humanCanMove()) {
            return;
        }
        if (selected != null && spot.square() != null && targets.contains(spot.square())) {
            playSelectedTo(spot.square());
            pressPlayed = true;
            return;
        }
        Game game = current();
        if (spot.isHand()) {
            if (spot.handSide() == game.turn() && game.hand(game.turn()).contains(spot.handType())) {
                select(spot, game.emptySquares(), new Piece(spot.handType(), game.turn(), false));
            } else {
                clearSelection();
                refresh();
            }
            return;
        }
        Piece piece = game.pieceAt(spot.square());
        if (piece != null && piece.owner() == game.turn()) {
            select(spot, game.targets(spot.square()), piece);
        } else {
            clearSelection();
            refresh();
        }
    }

    private void select(Spot spot, List<Square> reachable, Piece piece) {
        selected = spot;
        targets = reachable;
        dragging = true;
        refresh();
        double x = spot.isHand() ? board.trayX(spot.handType()) : board.squareX(spot.square());
        double y = spot.isHand() ? board.trayY(spot.handSide()) : board.squareY(spot.square());
        board.startDrag(piece, x, y);
    }

    @Override
    public void released(Spot spot) {
        if (pressPlayed || !dragging) {
            return;
        }
        dragging = false;
        board.endDrag();
        if (spot != null && spot.square() != null && targets.contains(spot.square())) {
            playSelectedTo(spot.square());
        }
    }

    private void playSelectedTo(Square to) {
        Game game = current();
        Move move = selected.isHand()
                ? Move.drop(selected.handType(), to)
                : new Move(game.pieceAt(selected.square()).type(), selected.square(), to);
        play(move);
    }

    private void play(Move move) {
        Game before = current();
        Game after = before.after(move);
        moveLines.add(String.format("%2d. %s", moves.size() + 1, notation(before, after, move)));
        moveList.scrollTo(moveLines.size() - 1);
        history.add(after);
        moves.add(move);
        clearSelection();
        refresh();
        board.animate(before, move, this::computerMoveIfDue);
    }

    static String notation(Game before, Game after, Move move) {
        char label = Piece.label(move.type(), before.turn());
        if (move.isDrop()) {
            return label + " drop " + move.to();
        }
        Piece moved = after.pieceAt(move.to());
        boolean upgradedNow = moved.upgraded() && !before.pieceAt(move.from()).upgraded();
        String separator = before.pieceAt(move.to()) == null ? "-" : "x";
        return label + " " + move.from() + separator + move.to() + (upgradedNow ? "+" : "");
    }

    private void computerMoveIfDue() {
        Game game = current();
        Computer.Level level = level();
        if (level == null || game.isOver() || game.turn() != computerSide() || thinking) {
            return;
        }
        thinking = true;
        refresh();
        int token = ++searchToken;
        Game position = game.copy();
        Thread thread = new Thread(() -> {
            long start = System.currentTimeMillis();
            Move move = Computer.choose(position, level, random);
            long rest = MINIMUM_THINKING_MILLIS - (System.currentTimeMillis() - start);
            try {
                if (rest > 0) {
                    Thread.sleep(rest);
                }
            } catch (InterruptedException e) {
                return;
            }
            Platform.runLater(() -> {
                if (token == searchToken) {
                    thinking = false;
                    play(move);
                }
            });
        }, "computer-player");
        thread.setDaemon(true);
        thread.start();
    }

    private void cancelSearch() {
        searchToken++;
        thinking = false;
    }

    private void clearSelection() {
        selected = null;
        targets = List.of();
        dragging = false;
        board.endDrag();
    }

    private void refresh() {
        Game game = current();
        board.show(game, selected, targets, moves.isEmpty() ? null : moves.get(moves.size() - 1));
        undo.setDisable(moves.isEmpty());
        if (game.isOver()) {
            status.setText(game.winner().title() + " wins");
            detail.setText("The king has been captured.");
        } else if (thinking) {
            status.setText(game.turn().title() + " is thinking");
            detail.setText("");
        } else {
            status.setText(game.turn().title() + " to move");
            detail.setText(game.kingInDanger(game.turn()) ? "Your king can be captured." : "");
        }
    }

}
