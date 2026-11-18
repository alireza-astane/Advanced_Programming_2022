package generals.logic;

public enum Side {
    WHITE(1), BLACK(-1);

    /** Direction this side's pieces advance along the rows. */
    public final int forward;

    Side(int forward) {
        this.forward = forward;
    }

    public Side opponent() {
        return this == WHITE ? BLACK : WHITE;
    }

    public String title() {
        return this == WHITE ? "White" : "Black";
    }

    public boolean promotesOn(int row) {
        return this == WHITE ? row > 3 : row < 3;
    }
}
