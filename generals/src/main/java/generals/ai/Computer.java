package generals.ai;

import generals.logic.Game;
import generals.logic.Move;
import generals.logic.Piece;
import generals.logic.PieceType;
import generals.logic.Side;
import generals.logic.Square;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Alpha-beta search over {@link Game} positions. */
public final class Computer {

    public enum Level {
        EASY("Easy", 1, 120, 0),
        NORMAL("Normal", 3, 20, 0),
        HARD("Hard", 16, 0, 1200);

        public final String title;
        final int depth;
        final int noise;
        final long millis;

        Level(String title, int depth, int noise, long millis) {
            this.title = title;
            this.depth = depth;
            this.noise = noise;
            this.millis = millis;
        }
    }

    private static final int WIN = 1_000_000;

    private final long deadline;
    private long nodes;
    private boolean stopped;

    private Computer(long deadline) {
        this.deadline = deadline;
    }

    public static Move choose(Game game, Level level, Random random) {
        long deadline = level.millis == 0 ? Long.MAX_VALUE : System.currentTimeMillis() + level.millis;
        return new Computer(deadline).searchRoot(game, level, random);
    }

    private Move searchRoot(Game game, Level level, Random random) {
        List<Move> moves = ordered(game);
        Move best = moves.get(0);
        for (int depth = level.millis == 0 ? level.depth : 1; depth <= level.depth; depth++) {
            Move bestHere = null;
            int bestScore = -2 * WIN;
            for (Move move : moves) {
                int alpha = level.noise == 0 ? bestScore : -2 * WIN;
                int score = -search(game.after(move), depth - 1, -2 * WIN, -alpha, 1);
                if (stopped) {
                    return best;
                }
                if (level.noise > 0) {
                    score += random.nextInt(2 * level.noise + 1) - level.noise;
                }
                if (score > bestScore) {
                    bestScore = score;
                    bestHere = move;
                }
            }
            best = bestHere;
            moves.remove(best);
            moves.add(0, best);
            if (bestScore > WIN / 2) {
                break;
            }
        }
        return best;
    }

    private int search(Game game, int depth, int alpha, int beta, int ply) {
        if (game.isOver()) {
            return -(WIN - ply);
        }
        if (depth == 0) {
            return game.kingInDanger(game.turn().opponent()) ? WIN - ply - 1 : evaluate(game);
        }
        if (++nodes % 2048 == 0 && System.currentTimeMillis() > deadline) {
            stopped = true;
        }
        if (stopped) {
            return 0;
        }
        int best = -2 * WIN;
        for (Move move : ordered(game)) {
            int score = -search(game.after(move), depth - 1, -beta, -alpha, ply + 1);
            if (stopped) {
                return 0;
            }
            best = Math.max(best, score);
            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                break;
            }
        }
        return best;
    }

    /** Captures first, biggest victim first, so that alpha-beta cuts early. */
    private static List<Move> ordered(Game game) {
        List<Move> moves = new ArrayList<>(game.legalMoves());
        moves.sort(Comparator.comparingInt((Move move) -> {
            Piece victim = game.pieceAt(move.to());
            return victim == null ? 0 : -value(victim.type(), victim.upgraded());
        }));
        return moves;
    }

    static int evaluate(Game game) {
        Side side = game.turn();
        int score = 0;
        for (Square square : Game.squares()) {
            Piece piece = game.pieceAt(square);
            if (piece != null) {
                int value = value(piece.type(), piece.upgraded()) + game.targets(square).size() * 3;
                score += piece.owner() == side ? value : -value;
            }
        }
        for (PieceType type : game.hand(side)) {
            score += value(type, false) + 25;
        }
        for (PieceType type : game.hand(side.opponent())) {
            score -= value(type, false) + 25;
        }
        return score;
    }

    private static int value(PieceType type, boolean upgraded) {
        return switch (type) {
            case KING -> WIN / 4;
            case GOLDEN_GENERAL -> 420;
            case SILVER_GENERAL -> upgraded ? 600 : 360;
            case BISHOP -> upgraded ? 720 : 400;
            case LANCE -> upgraded ? 740 : 260;
            case PAWN -> upgraded ? 400 : 100;
        };
    }
}
