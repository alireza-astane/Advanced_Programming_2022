package generals.gui;

import generals.logic.Piece;
import generals.logic.PieceType;
import generals.logic.Side;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

/**
 * Draws a piece as a five-sided tile pointing the way it advances. The marks around the
 * symbol show how it moves: a dot is one step, two dots are two, a line is any distance.
 */
final class PieceArt {
    private static final Color LIGHT_TILE = Color.web("#f2e6ca");
    private static final Color LIGHT_EDGE = Color.web("#a98f62");
    private static final Color LIGHT_INK = Color.web("#2b2622");
    private static final Color DARK_TILE = Color.web("#332d29");
    private static final Color DARK_EDGE = Color.web("#14110f");
    private static final Color DARK_INK = Color.web("#efe3c8");
    private static final Color LIGHT_UPGRADED = Color.web("#b23a2e");
    private static final Color DARK_UPGRADED = Color.web("#ef8a72");

    private static final double[] TILE_X = {0, 0.31, 0.39, -0.39, -0.31};
    private static final double[] TILE_Y = {-0.47, -0.31, 0.44, 0.44, -0.31};

    private PieceArt() {
    }

    static void draw(GraphicsContext g, Piece piece, double x, double y, double size) {
        boolean light = piece.owner() == Side.WHITE;
        g.save();
        g.translate(x, y);
        g.scale(size, size);

        g.save();
        g.translate(0, 0.022);
        g.setFill(Color.rgb(40, 25, 10, 0.28));
        if (!light) {
            g.rotate(180);
        }
        g.fillPolygon(TILE_X, TILE_Y, 5);
        g.restore();

        if (!light) {
            g.rotate(180);
        }
        g.setFill(light ? LIGHT_TILE : DARK_TILE);
        g.fillPolygon(TILE_X, TILE_Y, 5);
        g.setStroke(light ? LIGHT_EDGE : DARK_EDGE);
        g.setLineWidth(0.016);
        g.strokePolygon(TILE_X, TILE_Y, 5);

        Color ink = piece.upgraded()
                ? (light ? LIGHT_UPGRADED : DARK_UPGRADED)
                : (light ? LIGHT_INK : DARK_INK);
        g.setFill(ink);
        g.setStroke(ink);
        g.setLineCap(StrokeLineCap.ROUND);
        symbol(g, piece.type());
        for (PieceType.Step step : piece.type().steps(piece.upgraded())) {
            mark(g, step);
        }
        g.restore();
    }

    private static void symbol(GraphicsContext g, PieceType type) {
        g.setLineWidth(0.038);
        switch (type) {
            case KING -> g.fillPolygon(
                    new double[]{-0.125, -0.15, -0.065, 0, 0.065, 0.15, 0.125},
                    new double[]{0.135, -0.05, 0.03, -0.1, 0.03, -0.05, 0.135}, 7);
            case GOLDEN_GENERAL -> g.fillOval(-0.105, -0.07, 0.21, 0.21);
            case SILVER_GENERAL -> g.strokeOval(-0.09, -0.055, 0.18, 0.18);
            case BISHOP -> {
                g.strokeLine(-0.085, -0.05, 0.085, 0.12);
                g.strokeLine(0.085, -0.05, -0.085, 0.12);
            }
            case LANCE -> {
                g.strokeLine(0, 0.14, 0, -0.04);
                g.fillPolygon(new double[]{0, 0.06, -0.06}, new double[]{-0.1, 0, 0}, 3);
            }
            case PAWN -> g.fillPolygon(new double[]{0, 0.08, -0.08}, new double[]{-0.04, 0.11, 0.11}, 3);
        }
    }

    private static void mark(GraphicsContext g, PieceType.Step step) {
        double x = step.columns() * 0.235;
        double y = 0.035 - step.rows() * 0.255;
        double dot = 0.024;
        if (step.range() == 1) {
            g.fillOval(x - dot, y - dot, 2 * dot, 2 * dot);
        } else if (step.range() == 2) {
            double dx = step.columns() * 0.033;
            double dy = -step.rows() * 0.033;
            g.fillOval(x + dx - dot * 0.8, y + dy - dot * 0.8, 1.6 * dot, 1.6 * dot);
            g.fillOval(x - dx - dot * 0.8, y - dy - dot * 0.8, 1.6 * dot, 1.6 * dot);
        } else {
            g.setLineWidth(0.024);
            g.strokeLine(x - step.columns() * 0.05, y + step.rows() * 0.05,
                    x + step.columns() * 0.05, y - step.rows() * 0.05);
        }
    }
}
