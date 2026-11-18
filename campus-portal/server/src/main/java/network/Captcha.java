package network;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.SecureRandom;

/** A four digit code and a slightly distorted picture of it. */
public record Captcha(String code, byte[] image) {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int WIDTH = 150;
    private static final int HEIGHT = 52;

    public static Captcha generate() {
        String code = String.format("%04d", RANDOM.nextInt(10000));
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(245, 243, 238));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        drawNoise(g);
        g.setFont(new Font(Font.SERIF, Font.BOLD, 32));
        for (int i = 0; i < code.length(); i++) {
            drawDigit(g, code.substring(i, i + 1), 18 + i * 30);
        }
        g.dispose();
        return new Captcha(code, png(image));
    }

    private static void drawNoise(Graphics2D g) {
        g.setStroke(new BasicStroke(1.3f));
        for (int i = 0; i < 7; i++) {
            g.setColor(new Color(120 + RANDOM.nextInt(90), 120 + RANDOM.nextInt(90), 120 + RANDOM.nextInt(90)));
            g.drawLine(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
        }
    }

    private static void drawDigit(Graphics2D g, String digit, int x) {
        var original = g.getTransform();
        int y = 34 + RANDOM.nextInt(6);
        g.rotate((RANDOM.nextDouble() - 0.5) * 0.5, x + 8.0, y - 10.0);
        g.setColor(new Color(30 + RANDOM.nextInt(50), 35 + RANDOM.nextInt(50), 45 + RANDOM.nextInt(50)));
        g.drawString(digit, x, y);
        g.setTransform(original);
    }

    private static byte[] png(BufferedImage image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return bytes.toByteArray();
    }
}
