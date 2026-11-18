package generals.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class Game {
    public static final int SIZE = 5;

    private static final PieceType[] BACK_ROW = {
            PieceType.KING, PieceType.GOLDEN_GENERAL, PieceType.SILVER_GENERAL, PieceType.BISHOP, PieceType.LANCE
    };

    private final Piece[][] board = new Piece[SIZE][SIZE];
    private final Map<Side, List<PieceType>> hands = new EnumMap<>(Side.class);
    private Side turn = Side.BLACK;
    private Side winner;

    public Game() {
        hands.put(Side.WHITE, new ArrayList<>());
        hands.put(Side.BLACK, new ArrayList<>());
        for (int i = 0; i < SIZE; i++) {
            set(new Square(i + 1, 1), new Piece(BACK_ROW[i], Side.WHITE, false));
            set(new Square(SIZE - i, SIZE), new Piece(BACK_ROW[i], Side.BLACK, false));
        }
        set(new Square(1, 2), new Piece(PieceType.PAWN, Side.WHITE, false));
        set(new Square(SIZE, SIZE - 1), new Piece(PieceType.PAWN, Side.BLACK, false));
    }

    private Game(Game other) {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(other.board[i], 0, board[i], 0, SIZE);
        }
        hands.put(Side.WHITE, new ArrayList<>(other.hands.get(Side.WHITE)));
        hands.put(Side.BLACK, new ArrayList<>(other.hands.get(Side.BLACK)));
        turn = other.turn;
        winner = other.winner;
    }

    /** An empty board, for setting up positions. */
    public static Game empty(Side turn) {
        Game game = new Game();
        for (Piece[] column : game.board) {
            java.util.Arrays.fill(column, null);
        }
        game.turn = turn;
        return game;
    }

    public Game copy() {
        return new Game(this);
    }

    public void set(Square square, Piece piece) {
        board[square.column() - 1][square.row() - 1] = piece;
    }

    public void addToHand(Side side, PieceType type) {
        hands.get(side).add(type);
    }

    public Piece pieceAt(Square square) {
        return board[square.column() - 1][square.row() - 1];
    }

    public List<PieceType> hand(Side side) {
        return Collections.unmodifiableList(hands.get(side));
    }

    public Side turn() {
        return turn;
    }

    public Side winner() {
        return winner;
    }

    public boolean isOver() {
        return winner != null;
    }

    /** Squares the piece on {@code from} can reach, ignoring whose turn it is. */
    public List<Square> targets(Square from) {
        List<Square> targets = new ArrayList<>();
        Piece piece = pieceAt(from);
        if (piece == null) {
            return targets;
        }
        int forward = piece.owner().forward;
        for (PieceType.Step step : piece.type().steps(piece.upgraded())) {
            Square next = from;
            for (int i = 0; i < step.range(); i++) {
                next = next.plus(step.columns() * forward, step.rows() * forward);
                if (!next.onBoard()) {
                    break;
                }
                Piece blocker = pieceAt(next);
                if (blocker == null || blocker.owner() != piece.owner()) {
                    targets.add(next);
                }
                if (blocker != null) {
                    break;
                }
            }
        }
        return targets;
    }

    public List<Square> emptySquares() {
        List<Square> squares = new ArrayList<>();
        for (Square square : squares()) {
            if (pieceAt(square) == null) {
                squares.add(square);
            }
        }
        return squares;
    }

    public static List<Square> squares() {
        List<Square> squares = new ArrayList<>(SIZE * SIZE);
        for (int column = 1; column <= SIZE; column++) {
            for (int row = 1; row <= SIZE; row++) {
                squares.add(new Square(column, row));
            }
        }
        return squares;
    }

    public List<Move> legalMoves() {
        List<Move> moves = new ArrayList<>();
        if (isOver()) {
            return moves;
        }
        for (Square from : squares()) {
            Piece piece = pieceAt(from);
            if (piece != null && piece.owner() == turn) {
                for (Square to : targets(from)) {
                    moves.add(new Move(piece.type(), from, to));
                }
            }
        }
        List<Square> empty = emptySquares();
        for (PieceType type : new LinkedHashSet<>(hands.get(turn))) {
            for (Square to : empty) {
                moves.add(Move.drop(type, to));
            }
        }
        return moves;
    }

    public boolean isLegal(Move move) {
        if (isOver() || !move.to().onBoard()) {
            return false;
        }
        if (move.isDrop()) {
            return hands.get(turn).contains(move.type()) && pieceAt(move.to()) == null;
        }
        if (!move.from().onBoard()) {
            return false;
        }
        Piece piece = pieceAt(move.from());
        return piece != null && piece.owner() == turn && piece.type() == move.type()
                && targets(move.from()).contains(move.to());
    }

    /** Plays the move if it is legal and reports whether it was. */
    public boolean play(Move move) {
        if (!isLegal(move)) {
            return false;
        }
        apply(move);
        return true;
    }

    /** The position after a move already known to be legal. */
    public Game after(Move move) {
        Game next = copy();
        next.apply(move);
        return next;
    }

    private void apply(Move move) {
        Piece moved;
        if (move.isDrop()) {
            hands.get(turn).remove(move.type());
            moved = new Piece(move.type(), turn, false);
        } else {
            Piece piece = pieceAt(move.from());
            set(move.from(), null);
            boolean upgraded = piece.upgraded()
                    || (piece.type().canUpgrade && turn.promotesOn(move.to().row()));
            moved = new Piece(piece.type(), turn, upgraded);
            Piece captured = pieceAt(move.to());
            if (captured != null) {
                hands.get(turn).add(captured.type());
                if (captured.type() == PieceType.KING) {
                    winner = turn;
                }
            }
        }
        set(move.to(), moved);
        turn = turn.opponent();
    }

    public Square kingOf(Side side) {
        for (Square square : squares()) {
            Piece piece = pieceAt(square);
            if (piece != null && piece.type() == PieceType.KING && piece.owner() == side) {
                return square;
            }
        }
        return null;
    }

    public boolean kingInDanger(Side side) {
        Square king = kingOf(side);
        if (king == null) {
            return false;
        }
        for (Square square : squares()) {
            Piece piece = pieceAt(square);
            if (piece != null && piece.owner() != side && targets(square).contains(king)) {
                return true;
            }
        }
        return false;
    }
}
