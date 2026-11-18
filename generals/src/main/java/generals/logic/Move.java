package generals.logic;

/** A move on the board, or a drop from the hand when {@code from} is null. */
public record Move(PieceType type, Square from, Square to) {

    public static Move drop(PieceType type, Square to) {
        return new Move(type, null, to);
    }

    public boolean isDrop() {
        return from == null;
    }
}
