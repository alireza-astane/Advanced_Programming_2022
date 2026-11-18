package generals.ai;

import generals.logic.Game;
import generals.logic.Move;
import generals.logic.Piece;
import generals.logic.PieceType;
import generals.logic.Side;
import generals.logic.Square;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComputerTest {

    @ParameterizedTest
    @EnumSource(Computer.Level.class)
    void capturesAnExposedKing(Computer.Level level) {
        Game game = Game.empty(Side.WHITE);
        game.set(new Square(1, 1), new Piece(PieceType.KING, Side.WHITE, false));
        game.set(new Square(3, 1), new Piece(PieceType.LANCE, Side.WHITE, false));
        game.set(new Square(3, 5), new Piece(PieceType.KING, Side.BLACK, false));
        Move move = Computer.choose(game, level == Computer.Level.EASY ? Computer.Level.NORMAL : level, new Random(1));
        assertEquals(new Square(3, 5), move.to());
    }

    @Test
    void movesTheKingOutOfDanger() {
        Game game = Game.empty(Side.BLACK);
        game.set(new Square(3, 5), new Piece(PieceType.KING, Side.BLACK, false));
        game.set(new Square(3, 1), new Piece(PieceType.LANCE, Side.WHITE, false));
        game.set(new Square(1, 1), new Piece(PieceType.KING, Side.WHITE, false));
        Game next = game.after(Computer.choose(game, Computer.Level.NORMAL, new Random(1)));
        assertTrue(!next.kingInDanger(Side.BLACK));
    }

    @ParameterizedTest
    @EnumSource(value = Computer.Level.class, names = {"EASY", "NORMAL"})
    void selfPlayOnlyMakesLegalMoves(Computer.Level level) {
        Random random = new Random(3);
        Game game = new Game();
        for (int i = 0; i < 150 && !game.isOver(); i++) {
            assertTrue(game.play(Computer.choose(game, level, random)));
        }
    }

    @Test
    void normalBeatsEasyMostOfTheTime() {
        Random random = new Random(11);
        int wins = 0;
        int games = 12;
        for (int i = 0; i < games; i++) {
            Side normal = i % 2 == 0 ? Side.BLACK : Side.WHITE;
            Game game = new Game();
            for (int ply = 0; ply < 200 && !game.isOver(); ply++) {
                Computer.Level level = game.turn() == normal ? Computer.Level.NORMAL : Computer.Level.EASY;
                game.play(Computer.choose(game, level, random));
            }
            if (game.winner() == normal) {
                wins++;
            }
        }
        assertTrue(wins >= 10, "normal won " + wins + " of " + games);
    }

    @Test
    void hardAnswersWithinItsTimeLimit() {
        long start = System.currentTimeMillis();
        Game game = new Game();
        assertTrue(game.isLegal(Computer.choose(game, Computer.Level.HARD, new Random(5))));
        assertTrue(System.currentTimeMillis() - start < 2500);
    }
}
