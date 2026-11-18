package generals.gui;

import generals.logic.Game;
import generals.logic.Move;
import generals.logic.Piece;
import generals.logic.PieceType;
import generals.logic.Side;
import generals.logic.Square;
import javafx.animation.AnimationTimer;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** The board and both hands, painted on one canvas so pieces can travel between them. */
final class BoardCanvas extends Region {

    /** A place the player can point at: a board square or a piece type in a hand. */
    record Spot(Square square, Side handSide, PieceType handType) {
        static Spot board(Square square) {
            return new Spot(square, null, null);
        }

        static Spot hand(Side side, PieceType type) {
            return new Spot(null, side, type);
        }

        boolean isHand() {
            return square == null;
        }
    }

    interface Listener {
        void pressed(Spot spot);

        void released(Spot spot);
    }

    private static final List<PieceType> TRAY = List.of(PieceType.GOLDEN_GENERAL, PieceType.SILVER_GENERAL,
            PieceType.BISHOP, PieceType.LANCE, PieceType.PAWN);
    private static final long SLIDE_NANOS = 170_000_000L;

    private static final Color WOOD = Color.web("#d9b880");
    private static final Color GRAIN = Color.web("#b8925a", 0.20);
    private static final Color LINE = Color.web("#5d4526");
    private static final Color TRAY_FILL = Color.web("#e6dcc8");
    private static final Color TRAY_ACTIVE = Color.web("#dccfb4");
    private static final Color LAST_MOVE = Color.web("#f4dc8e", 0.55);
    private static final Color SELECTED = Color.web("#6f8f5f", 0.55);
    private static final Color TARGET = Color.web("#3f5f37", 0.62);
    private static final Color DANGER = Color.web("#b23a2e");

    private final Canvas canvas = new Canvas();
    private final Listener listener;

    private Game game = new Game();
    private Spot selected;
    private Set<Square> targets = Set.of();
    private Move lastMove;
    private double renderScale;

    private Piece dragged;
    private double dragX;
    private double dragY;

    private AnimationTimer timer;
    private Piece sliding;
    private double slideFromX;
    private double slideFromY;
    private Square slideTo;
    private double slideProgress;
    private Runnable afterSlide;

    private double cell;
    private double boardX;
    private double boardY;
    private double trayHeight;
    private double blackTrayY;
    private double whiteTrayY;

    BoardCanvas(Listener listener) {
        this.listener = listener;
        canvas.setManaged(false);
        canvas.setMouseTransparent(true);
        getChildren().add(canvas);
        setMinSize(320, 400);
        setOnMousePressed(this::onPressed);
        setOnMouseDragged(this::onDragged);
        setOnMouseReleased(this::onReleased);
    }

    void show(Game game, Spot selected, Collection<Square> targets, Move lastMove) {
        this.game = game;
        this.selected = selected;
        this.targets = Set.copyOf(targets);
        this.lastMove = lastMove;
        paint();
    }

    void setRenderScale(double renderScale) {
        this.renderScale = renderScale;
        requestLayout();
    }

    void startDrag(Piece piece, double x, double y) {
        dragged = piece;
        dragX = x;
        dragY = y;
        paint();
    }

    void endDrag() {
        dragged = null;
        paint();
    }

    /** Slides the piece just placed by {@code move}, which {@code before} had not yet played. */
    void animate(Game before, Move move, Runnable done) {
        finishSlide();
        Side side = before.turn();
        Piece piece = before.after(move).pieceAt(move.to());
        if (move.isDrop()) {
            slideFromX = trayX(move.type());
            slideFromY = trayY(side);
        } else {
            slideFromX = squareX(move.from());
            slideFromY = squareY(move.from());
        }
        sliding = piece;
        slideTo = move.to();
        slideProgress = 0;
        afterSlide = done;
        long start = System.nanoTime();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                slideProgress = Math.min(1, (now - start) / (double) SLIDE_NANOS);
                if (slideProgress >= 1) {
                    finishSlide();
                } else {
                    paint();
                }
            }
        };
        timer.start();
    }

    /** Freezes the current slide at a point between 0 and 1, for rendering single frames. */
    void holdSlide(double progress) {
        if (timer != null) {
            timer.stop();
        }
        slideProgress = progress;
        paint();
    }

    void finishSlide() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        sliding = null;
        slideTo = null;
        Runnable done = afterSlide;
        afterSlide = null;
        paint();
        if (done != null) {
            done.run();
        }
    }

    Spot spotAt(double x, double y) {
        if (x >= boardX && x < boardX + 5 * cell) {
            int index = (int) ((x - boardX) / cell);
            if (y >= boardY && y < boardY + 5 * cell) {
                return Spot.board(new Square(index + 1, Game.SIZE - (int) ((y - boardY) / cell)));
            }
            if (index < TRAY.size()) {
                if (y >= blackTrayY && y < blackTrayY + trayHeight) {
                    return Spot.hand(Side.BLACK, TRAY.get(index));
                }
                if (y >= whiteTrayY && y < whiteTrayY + trayHeight) {
                    return Spot.hand(Side.WHITE, TRAY.get(index));
                }
            }
        }
        return null;
    }

    double squareX(Square square) {
        return boardX + (square.column() - 0.5) * cell;
    }

    double squareY(Square square) {
        return boardY + (Game.SIZE - square.row() + 0.5) * cell;
    }

    double trayX(PieceType type) {
        return boardX + (TRAY.indexOf(type) + 0.5) * cell;
    }

    double trayY(Side side) {
        return (side == Side.BLACK ? blackTrayY : whiteTrayY) + trayHeight / 2;
    }

    private void onPressed(MouseEvent event) {
        Spot spot = spotAt(event.getX(), event.getY());
        dragX = event.getX();
        dragY = event.getY();
        if (spot != null) {
            listener.pressed(spot);
        }
    }

    private void onDragged(MouseEvent event) {
        if (dragged != null) {
            dragX = event.getX();
            dragY = event.getY();
            paint();
        }
    }

    private void onReleased(MouseEvent event) {
        listener.released(spotAt(event.getX(), event.getY()));
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double height = getHeight();
        double scale = renderScale > 0 ? renderScale
                : getScene() != null && getScene().getWindow() != null ? getScene().getWindow().getRenderScaleX() : 1;
        canvas.setWidth(Math.ceil(width * scale));
        canvas.setHeight(Math.ceil(height * scale));
        canvas.setScaleX(1 / scale);
        canvas.setScaleY(1 / scale);
        canvas.setLayoutX((width - canvas.getWidth()) / 2);
        canvas.setLayoutY((height - canvas.getHeight()) / 2);

        cell = Math.min(width / 5.3, height / 7.1);
        trayHeight = cell * 0.86;
        double gap = cell * 0.18;
        boardX = (width - 5 * cell) / 2;
        blackTrayY = (height - (5 * cell + 2 * trayHeight + 2 * gap)) / 2;
        boardY = blackTrayY + trayHeight + gap;
        whiteTrayY = boardY + 5 * cell + gap;
        paint();
    }

    private void paint() {
        if (cell <= 0) {
            return;
        }
        GraphicsContext g = canvas.getGraphicsContext2D();
        double scale = canvas.getWidth() / Math.max(1, getWidth());
        g.save();
        g.setTransform(scale, 0, 0, scale, 0, 0);
        g.clearRect(0, 0, getWidth(), getHeight());
        paintTray(g, Side.BLACK, blackTrayY);
        paintTray(g, Side.WHITE, whiteTrayY);
        paintBoard(g);
        paintPieces(g);
        paintTargets(g);
        if (sliding != null) {
            double t = 1 - Math.pow(1 - slideProgress, 3);
            double x = slideFromX + (squareX(slideTo) - slideFromX) * t;
            double y = slideFromY + (squareY(slideTo) - slideFromY) * t;
            PieceArt.draw(g, sliding, x, y, cell * 0.92);
        }
        if (dragged != null) {
            PieceArt.draw(g, dragged, dragX, dragY, cell);
        }
        if (game.isOver() && sliding == null) {
            paintResult(g);
        }
        g.restore();
    }

    private void paintBoard(GraphicsContext g) {
        double size = 5 * cell;
        g.setFill(WOOD);
        g.fillRect(boardX, boardY, size, size);
        paintGrain(g, size);

        if (lastMove != null) {
            g.setFill(LAST_MOVE);
            if (!lastMove.isDrop()) {
                fillSquare(g, lastMove.from());
            }
            fillSquare(g, lastMove.to());
        }
        if (selected != null && !selected.isHand()) {
            g.setFill(SELECTED);
            fillSquare(g, selected.square());
        }

        g.setStroke(LINE);
        g.setLineWidth(1);
        for (int i = 0; i <= Game.SIZE; i++) {
            double offset = Math.round(i * cell) + 0.5;
            g.strokeLine(boardX + offset, boardY, boardX + offset, boardY + size);
            g.strokeLine(boardX, boardY + offset, boardX + size, boardY + offset);
        }
        g.setLineWidth(2);
        g.strokeRect(boardX, boardY, size, size);

        g.setFill(LINE);
        double dot = Math.max(3, cell * 0.05);
        for (int column = 2; column <= 3; column++) {
            for (int row = 2; row <= 3; row++) {
                double x = boardX + (column == 2 ? 1 : 4) * cell;
                double y = boardY + (row == 2 ? 2 : 3) * cell;
                g.fillOval(x - dot, y - dot, 2 * dot, 2 * dot);
            }
        }

        g.setFont(Font.font("Serif", cell * 0.13));
        g.setFill(LINE.deriveColor(0, 1, 1, 0.8));
        g.setTextBaseline(VPos.CENTER);
        g.setTextAlign(TextAlignment.CENTER);
        for (int i = 1; i <= Game.SIZE; i++) {
            g.fillText(String.valueOf(i), boardX + (i - 0.5) * cell, boardY + size + cell * 0.08);
            g.fillText(String.valueOf(i), boardX - cell * 0.09, boardY + (Game.SIZE - i + 0.5) * cell);
        }
    }

    private void paintGrain(GraphicsContext g, double size) {
        Random random = new Random(7);
        g.setStroke(GRAIN);
        g.setLineWidth(1);
        for (int i = 0; i < 34; i++) {
            double y = boardY + random.nextDouble() * size;
            double bend = (random.nextDouble() - 0.5) * cell * 0.25;
            g.beginPath();
            g.moveTo(boardX + 1, y);
            g.bezierCurveTo(boardX + size * 0.3, y + bend, boardX + size * 0.7, y - bend, boardX + size - 1, y);
            g.stroke();
        }
    }

    private void fillSquare(GraphicsContext g, Square square) {
        g.fillRect(squareX(square) - cell / 2, squareY(square) - cell / 2, cell, cell);
    }

    private void paintPieces(GraphicsContext g) {
        for (Side side : Side.values()) {
            if (game.kingInDanger(side) && !game.isOver()) {
                Square king = game.kingOf(side);
                g.setStroke(DANGER);
                g.setLineWidth(Math.max(2, cell * 0.04));
                double inset = cell * 0.05;
                g.strokeRect(squareX(king) - cell / 2 + inset, squareY(king) - cell / 2 + inset,
                        cell - 2 * inset, cell - 2 * inset);
            }
        }
        for (Square square : Game.squares()) {
            Piece piece = game.pieceAt(square);
            boolean held = dragged != null && selected != null && square.equals(selected.square());
            if (piece != null && !held && !square.equals(slideTo)) {
                PieceArt.draw(g, piece, squareX(square), squareY(square), cell * 0.92);
            }
        }
    }

    private void paintTargets(GraphicsContext g) {
        for (Square square : targets) {
            double x = squareX(square);
            double y = squareY(square);
            if (game.pieceAt(square) == null) {
                double radius = cell * 0.1;
                g.setFill(TARGET);
                g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
            } else {
                double radius = cell * 0.44;
                g.setStroke(TARGET);
                g.setLineWidth(Math.max(2.5, cell * 0.05));
                g.strokeOval(x - radius, y - radius, 2 * radius, 2 * radius);
            }
        }
    }

    private void paintTray(GraphicsContext g, Side side, double y) {
        boolean active = game.turn() == side && !game.isOver();
        g.setFill(active ? TRAY_ACTIVE : TRAY_FILL);
        g.fillRoundRect(boardX, y, 5 * cell, trayHeight, cell * 0.08, cell * 0.08);

        Map<PieceType, Integer> counts = new HashMap<>();
        for (PieceType type : game.hand(side)) {
            counts.merge(type, 1, Integer::sum);
        }
        if (sliding != null && slideTo != null && lastMove != null && lastMove.isDrop()
                && game.turn() != side) {
            counts.merge(lastMove.type(), 1, Integer::sum);
        }
        for (PieceType type : TRAY) {
            int count = counts.getOrDefault(type, 0);
            if (dragged != null && selected != null && selected.isHand()
                    && selected.handSide() == side && selected.handType() == type) {
                count--;
            }
            if (count <= 0) {
                continue;
            }
            double x = trayX(type);
            double centerY = y + trayHeight / 2;
            if (selected != null && selected.isHand() && selected.handSide() == side && selected.handType() == type) {
                g.setFill(SELECTED);
                g.fillRoundRect(x - cell / 2, y, cell, trayHeight, cell * 0.08, cell * 0.08);
            }
            PieceArt.draw(g, new Piece(type, side, false), x, centerY, trayHeight * 0.92);
            if (count > 1) {
                paintCount(g, count, x + cell * 0.3, y + trayHeight * 0.2);
            }
        }
    }

    private void paintCount(GraphicsContext g, int count, double x, double y) {
        double radius = cell * 0.12;
        g.setFill(DANGER);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        g.setFill(Color.WHITE);
        g.setFont(Font.font("SansSerif", FontWeight.BOLD, cell * 0.15));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(String.valueOf(count), x, y);
    }

    private void paintResult(GraphicsContext g) {
        double height = cell * 0.9;
        double y = boardY + (5 * cell - height) / 2;
        g.setFill(Color.web("#26221e", 0.9));
        g.fillRect(boardX, y, 5 * cell, height);
        g.setFill(Color.web("#f4ead4"));
        g.setFont(Font.font("Serif", FontWeight.BOLD, cell * 0.36));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(game.winner().title() + " wins", boardX + 2.5 * cell, y + height / 2);
    }
}
