package generals.logic;

public record Piece(PieceType type, Side owner, boolean upgraded) {

    /** White pieces are upper case and black pieces lower case, as in the console format. */
    public char label() {
        return label(type, owner);
    }

    public static char label(PieceType type, Side owner) {
        return owner == Side.WHITE ? type.letter : Character.toLowerCase(type.letter);
    }
}
