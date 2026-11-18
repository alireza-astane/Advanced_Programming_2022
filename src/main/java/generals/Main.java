package generals;

import generals.logic.Game;
import generals.logic.Move;
import generals.logic.Piece;
import generals.logic.PieceType;
import generals.logic.Side;
import generals.logic.Square;

import java.io.PrintStream;
import java.util.Scanner;

/** The console version: reads moves such as {@code p 54 53} and prints the game after each one. */
public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    public static void run(Scanner in, PrintStream out) {
        Game game = new Game();
        while (in.hasNextLine()) {
            String line = in.nextLine().trim();
            if (line.equals("0")) {
                return;
            }
            Move move = parse(line, game);
            if (move != null) {
                game.play(move);
            }
            print(game, out);
            if (game.isOver()) {
                out.print(game.winner().name().toLowerCase() + " wins!");
                return;
            }
        }
    }

    /** Returns null when the line cannot describe a move in this position. */
    static Move parse(String line, Game game) {
        String[] parts = line.split("\\s+");
        if (parts.length != 3 || parts[0].length() != 1) {
            return null;
        }
        char label = parts[0].charAt(0);
        PieceType type = PieceType.ofLetter(label);
        Square from = parseSquare(parts[1]);
        Square to = parseSquare(parts[2]);
        if (type == null || from == null || to == null) {
            return null;
        }
        Side side = Character.isUpperCase(label) ? Side.WHITE : Side.BLACK;
        if (side != game.turn()) {
            return null;
        }
        return from.equals(new Square(0, 0)) ? Move.drop(type, to) : new Move(type, from, to);
    }

    private static Square parseSquare(String text) {
        if (text.length() != 2 || !Character.isDigit(text.charAt(0)) || !Character.isDigit(text.charAt(1))) {
            return null;
        }
        return new Square(text.charAt(0) - '0', text.charAt(1) - '0');
    }

    static void print(Game game, PrintStream out) {
        StringBuilder board = new StringBuilder();
        for (int row = 1; row <= Game.SIZE; row++) {
            for (int column = 1; column <= Game.SIZE; column++) {
                Piece piece = game.pieceAt(new Square(column, row));
                board.append(piece == null ? '-' : piece.label());
            }
        }
        out.println(board);
        out.println(hand(game, Side.BLACK));
        out.println(hand(game, Side.WHITE));
    }

    private static String hand(Game game, Side side) {
        StringBuilder hand = new StringBuilder();
        for (PieceType type : game.hand(side)) {
            hand.append(Piece.label(type, side));
        }
        return hand.toString();
    }
}
