package generals.logic;

public record Square(int column, int row) {

    public boolean onBoard() {
        return column >= 1 && column <= Game.SIZE && row >= 1 && row <= Game.SIZE;
    }

    public Square plus(int columns, int rows) {
        return new Square(column + columns, row + rows);
    }

    @Override
    public String toString() {
        return "" + column + row;
    }
}
