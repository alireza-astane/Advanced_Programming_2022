package generals.logic;

import java.util.ArrayList;
import java.util.List;

public enum PieceType {
    KING('K', false),
    GOLDEN_GENERAL('G', false),
    SILVER_GENERAL('S', true),
    BISHOP('B', true),
    LANCE('L', true),
    PAWN('P', true);

    /** One direction a piece can travel, written from White's point of view. */
    public record Step(int columns, int rows, int range) {
    }

    private static final int FAR = Game.SIZE - 1;
    private static final int[][] STRAIGHT = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static final int[][] DIAGONAL = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
    private static final int[][] FORWARD_DIAGONAL = {{1, 1}, {-1, 1}};
    private static final int[][] GOLD = {{0, 1}, {1, 1}, {-1, 1}, {1, 0}, {-1, 0}, {0, -1}};
    private static final int[][] SILVER = {{0, 1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};

    public final char letter;
    public final boolean canUpgrade;

    PieceType(char letter, boolean canUpgrade) {
        this.letter = letter;
        this.canUpgrade = canUpgrade;
    }

    public static PieceType ofLetter(char letter) {
        char upper = Character.toUpperCase(letter);
        for (PieceType type : values()) {
            if (type.letter == upper) {
                return type;
            }
        }
        return null;
    }

    public List<Step> steps(boolean upgraded) {
        List<Step> steps = new ArrayList<>();
        switch (this) {
            case KING -> {
                add(steps, STRAIGHT, 1);
                add(steps, DIAGONAL, 1);
            }
            case GOLDEN_GENERAL -> add(steps, GOLD, 1);
            case SILVER_GENERAL -> {
                if (upgraded) {
                    add(steps, STRAIGHT, 2);
                    add(steps, DIAGONAL, 2);
                } else {
                    add(steps, SILVER, 1);
                }
            }
            case BISHOP -> {
                if (upgraded) {
                    add(steps, DIAGONAL, FAR);
                    add(steps, STRAIGHT, 1);
                } else {
                    add(steps, FORWARD_DIAGONAL, FAR);
                }
            }
            case LANCE -> {
                if (upgraded) {
                    add(steps, STRAIGHT, FAR);
                } else {
                    steps.add(new Step(0, 1, FAR));
                }
            }
            case PAWN -> {
                if (upgraded) {
                    add(steps, GOLD, 1);
                } else {
                    steps.add(new Step(0, 1, 1));
                }
            }
        }
        return steps;
    }

    private static void add(List<Step> steps, int[][] directions, int range) {
        for (int[] direction : directions) {
            steps.add(new Step(direction[0], direction[1], range));
        }
    }
}
