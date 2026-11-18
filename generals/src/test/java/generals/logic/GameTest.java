package generals.logic;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static generals.logic.PieceType.*;
import static generals.logic.Side.BLACK;
import static generals.logic.Side.WHITE;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private static final Square CENTER = new Square(3, 3);

    private static Square at(int code) {
        return new Square(code / 10, code % 10);
    }

    private static Set<Square> squares(int... codes) {
        Set<Square> squares = new HashSet<>();
        for (int code : codes) {
            squares.add(at(code));
        }
        return squares;
    }

    private static Game with(Side turn, Square square, Piece piece) {
        Game game = Game.empty(turn);
        game.set(square, piece);
        return game;
    }

    private static Set<Square> reach(PieceType type, Side side, boolean upgraded) {
        return new HashSet<>(with(side, CENTER, new Piece(type, side, upgraded)).targets(CENTER));
    }

    @Test
    void startingPositionMatchesTheRules() {
        Game game = new Game();
        assertEquals(BLACK, game.turn());
        assertEquals(new Piece(KING, WHITE, false), game.pieceAt(at(11)));
        assertEquals(new Piece(LANCE, WHITE, false), game.pieceAt(at(51)));
        assertEquals(new Piece(PAWN, WHITE, false), game.pieceAt(at(12)));
        assertEquals(new Piece(KING, BLACK, false), game.pieceAt(at(55)));
        assertEquals(new Piece(LANCE, BLACK, false), game.pieceAt(at(15)));
        assertEquals(new Piece(PAWN, BLACK, false), game.pieceAt(at(54)));
    }

    @Test
    void piecesMoveAsDescribed() {
        assertEquals(squares(34), reach(PAWN, WHITE, false));
        assertEquals(squares(32), reach(PAWN, BLACK, false));
        assertEquals(squares(34, 35), reach(LANCE, WHITE, false));
        assertEquals(squares(24, 15, 44, 55), reach(BISHOP, WHITE, false));
        assertEquals(squares(34, 24, 44, 22, 42), reach(SILVER_GENERAL, WHITE, false));
        assertEquals(squares(34, 24, 44, 23, 43, 32), reach(GOLDEN_GENERAL, WHITE, false));
        assertEquals(squares(32, 22, 42, 23, 43, 34), reach(GOLDEN_GENERAL, BLACK, false));
        assertEquals(squares(22, 23, 24, 32, 34, 42, 43, 44), reach(KING, WHITE, false));
    }

    @Test
    void upgradedPiecesMoveAsDescribed() {
        assertEquals(reach(GOLDEN_GENERAL, WHITE, false), reach(PAWN, WHITE, true));
        assertEquals(squares(31, 32, 34, 35, 13, 23, 43, 53), reach(LANCE, WHITE, true));
        assertEquals(squares(11, 22, 44, 55, 15, 24, 42, 51, 32, 34, 23, 43), reach(BISHOP, WHITE, true));
        assertEquals(squares(31, 32, 34, 35, 13, 23, 43, 53, 11, 22, 44, 55, 15, 24, 42, 51),
                reach(SILVER_GENERAL, WHITE, true));
    }

    @Test
    void piecesCannotJumpButCanCaptureTheBlocker() {
        Game game = with(WHITE, at(31), new Piece(LANCE, WHITE, false));
        game.set(at(33), new Piece(PAWN, BLACK, false));
        assertEquals(squares(32, 33), new HashSet<>(game.targets(at(31))));

        game.set(at(33), new Piece(PAWN, WHITE, false));
        assertEquals(squares(32), new HashSet<>(game.targets(at(31))));
    }

    @Test
    void upgradedSilverCannotJumpOverAPiece() {
        Game game = with(WHITE, CENTER, new Piece(SILVER_GENERAL, WHITE, true));
        game.set(at(34), new Piece(PAWN, WHITE, false));
        assertFalse(game.targets(CENTER).contains(at(35)));
    }

    @Test
    void piecesUpgradeInTheLastTwoRows() {
        Game white = with(WHITE, CENTER, new Piece(PAWN, WHITE, false));
        assertTrue(white.play(new Move(PAWN, CENTER, at(34))));
        assertTrue(white.pieceAt(at(34)).upgraded());

        Game black = with(BLACK, CENTER, new Piece(PAWN, BLACK, false));
        assertTrue(black.play(new Move(PAWN, CENTER, at(32))));
        assertTrue(black.pieceAt(at(32)).upgraded());

        Game notYet = with(WHITE, at(32), new Piece(PAWN, WHITE, false));
        assertTrue(notYet.play(new Move(PAWN, at(32), CENTER)));
        assertFalse(notYet.pieceAt(CENTER).upgraded());
    }

    @Test
    void capturedPieceChangesSidesAndLosesItsUpgrade() {
        Game game = with(WHITE, CENTER, new Piece(GOLDEN_GENERAL, WHITE, false));
        game.set(at(34), new Piece(LANCE, BLACK, true));
        assertTrue(game.play(new Move(GOLDEN_GENERAL, CENTER, at(34))));
        assertEquals(List.of(LANCE), game.hand(WHITE));

    }

    @Test
    void droppedPieceIsPlainEvenInTheLastRowsAndNeedsAnEmptySquare() {
        Game game = Game.empty(WHITE);
        game.addToHand(WHITE, SILVER_GENERAL);
        game.set(at(44), new Piece(PAWN, BLACK, false));
        assertFalse(game.play(Move.drop(SILVER_GENERAL, at(44))));
        assertFalse(game.play(Move.drop(BISHOP, at(45))));
        assertTrue(game.play(Move.drop(SILVER_GENERAL, at(45))));
        assertFalse(game.pieceAt(at(45)).upgraded());
        assertTrue(game.hand(WHITE).isEmpty());
        assertEquals(BLACK, game.turn());
    }

    @Test
    void pieceDroppedInTheLastRowsUpgradesOnlyWhenItLaterMovesThere() {
        Game game = Game.empty(WHITE);
        game.addToHand(WHITE, PAWN);
        game.set(at(11), new Piece(KING, WHITE, false));
        game.set(at(55), new Piece(KING, BLACK, false));
        assertTrue(game.play(Move.drop(PAWN, at(34))));
        assertFalse(game.pieceAt(at(34)).upgraded());
        assertTrue(game.play(new Move(KING, at(55), at(54))));
        assertTrue(game.play(new Move(PAWN, at(34), at(35))));
        assertTrue(game.pieceAt(at(35)).upgraded());
    }

    @Test
    void forwardOnlyPieceDroppedOnTheLastRowCannotMove() {
        for (PieceType type : List.of(PAWN, LANCE, BISHOP)) {
            Game game = Game.empty(WHITE);
            game.addToHand(WHITE, type);
            assertTrue(game.play(Move.drop(type, at(35))));
            assertTrue(game.targets(at(35)).isEmpty(), type.name());
        }
    }

    @Test
    void leavingTheLastRowsDoesNotUpgrade() {
        Game game = with(WHITE, at(34), new Piece(SILVER_GENERAL, WHITE, false));
        assertTrue(game.play(new Move(SILVER_GENERAL, at(34), at(23))));
        assertFalse(game.pieceAt(at(23)).upgraded());
    }

    @Test
    void capturingTheKingEndsTheGame() {
        Game game = with(WHITE, CENTER, new Piece(KING, WHITE, false));
        game.set(at(34), new Piece(KING, BLACK, false));
        assertTrue(game.kingInDanger(BLACK));
        assertTrue(game.play(new Move(KING, CENTER, at(34))));
        assertEquals(WHITE, game.winner());
        assertTrue(game.legalMoves().isEmpty());
    }

    @Test
    void cannotMoveOutOfTurnOrWithTheWrongPiece() {
        Game game = new Game();
        assertFalse(game.play(new Move(PAWN, at(12), at(13))));
        assertFalse(game.play(new Move(LANCE, at(54), at(53))));
        assertTrue(game.play(new Move(PAWN, at(54), at(53))));
    }

    @Test
    void afterLeavesTheOriginalUntouched() {
        Game game = new Game();
        Game next = game.after(new Move(PAWN, at(54), at(53)));
        assertNotNull(game.pieceAt(at(54)));
        assertNull(next.pieceAt(at(54)));
        assertEquals(BLACK, game.turn());
    }
}
