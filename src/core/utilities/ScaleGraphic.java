package core.utilities;

import core.components.ScaleComponent;
import core.components.ScalePage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <h1>The graphic context</h1>
 * <p>represents the main graphic interaction with the application</p>
 *
 * @author Andrea Maruca
 * @since 1.0
 */
public record ScaleGraphic(
        ScalePage page,
        Graphics2D g2
) {
    private static final Map<String, Image> cachedImages = new HashMap<>();

    public void drawImage(Dim dim, String percorso) {
        Image image = cachedImages.computeIfAbsent(percorso, this::loadImage);

        int x = getX(dim.x());
        int y = getY(dim.y());
        int w = getX(dim.width());
        int h = getY(dim.height());

        g2().drawImage(image, x, y, w, h, null);
    }

    private Image loadImage(String percorso) {
        try {
            return ImageIO.read(Objects.requireNonNull(
                    ScaleGraphic.class.getResource("/" + percorso),
                    "Resource notFound " + percorso
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void draw(ScaleComponent component) {
        component.draw(this);
    }

    /**
     * Changes the drawing color
     *
     * @param newColor new color
     */
    public void setColor(Color newColor) {
        g2().setColor(newColor);
    }

    /**
     * Changes the drawing thickness
     *
     * @param thickness new thickness
     */
    public void changeDrawWidth(double thickness) {
        g2().setStroke(new BasicStroke(getX(thickness)));
    }

    /**
     * Changes the font to the custom one
     *
     * @param size font size
     */
    public void font(double size) {
        g2().setFont(FontLoader.getCustomFont(getX(size)));
    }

    /**
     * Draws the text passed at the position passed and automatically wraps<br>
     * (More expensive than {@link #drawText(Dim, String)})
     *
     * @param text           text to draw
     * @param labelDimension position and dimension of the text
     */
    public void drawWrapText(Dim labelDimension, String text) {
        Graphics2D g = g2();
        int x = getX(labelDimension.x() + 2);
        int y = getY(labelDimension.y() + 2);
        int width = getX(labelDimension.width() - 4);

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();

        int currentY = y + fm.getAscent();
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (ch == '\n') {
                g.drawString(line.toString(), x, currentY);
                line.setLength(0);
                currentY += lineHeight;
                continue;
            }

            line.append(ch);

            if (fm.stringWidth(line.toString()) > width) {
                line.deleteCharAt(line.length() - 1);
                g.drawString(line.toString(), x, currentY);
                currentY += lineHeight;

                line.setLength(0);

                if (ch != ' ') {
                    line.append(ch);
                }
            }
        }

        if (!line.isEmpty()) {
            g.drawString(line.toString(), x, currentY);
        }
    }

    /**
     * Draws the text at the specified position (starting from top-left)<br>
     *
     * @param dim   position and dimension of where to draw the text
     * @param text  text to draw
     * @param color text color
     *
     * @since 1.2
     * @author Andrea Maruca
     */
    public void drawTextLeft(Dim dim, String text, Color color) {
        Color oldColor = g2().getColor();
        g2().setColor(color);

        FontMetrics fm = g2().getFontMetrics();
        int ascent = fm.getAscent();

        int x = getX(dim.x() + 1);
        int y = getY(dim.y()) + ascent + 1;

        g2().drawString(text, x, y);
        g2().setColor(oldColor);
    }

    /**
     * Draws the text passed at the position passed and automatically wraps (starting from top-left)<br>
     * (More expensive than {@link #drawTextLeft(Dim, String, Color)})
     *
     * @param labelDimension position and dimension of the text
     * @param text           text to draw
     *
     * @since 1.2
     * @author Andrea Maruca
     */
    public void drawWrapTextLeft(Dim labelDimension, String text) {
        Graphics2D g = g2();
        int x = getX(labelDimension.x() + 1);
        int y = getY(labelDimension.y() + 1);
        int width = getX(labelDimension.width() - 2);

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        int ascent = fm.getAscent();

        int currentY = y + ascent;
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (ch == '\n') {
                g.drawString(line.toString(), x, currentY);
                line.setLength(0);
                currentY += lineHeight;
                continue;
            }

            line.append(ch);

            if (fm.stringWidth(line.toString()) > width) {
                line.deleteCharAt(line.length() - 1);
                g.drawString(line.toString(), x, currentY);
                currentY += lineHeight;

                line.setLength(0);

                if (ch != ' ') {
                    line.append(ch);
                }
            }
        }

        if (!line.isEmpty()) {
            g.drawString(line.toString(), x, currentY);
        }
    }

    public void drawWrapTextWithColor(String text, Dim labelDimension, Color color) {
        g2().setColor(color);
        drawWrapText(labelDimension, text);
    }


    public void drawText(Dim dim, String text) {
        g2().drawString(text, getX(dim.x() + 1), getY(dim.y() + 1));
    }

    /**
     * Draws the text at the specified position with the specified color<br>
     *
     * @param dim   position and dimension of where to draw the text
     * @param text  text to draw
     * @param color text color
     */
    public void drawText(Dim dim, String text, Color color) {
        Color oldColor = g2().getColor();
        g2().setColor(color);

        FontMetrics fm = g2().getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();

        int x = getX(dim.x()) + (getX(dim.width()) - textWidth) / 2;
        int yBase = getY(dim.y());
        int hPix = getY(dim.height());
        int y = yBase + (hPix + ascent - descent) / 2;

        g2().drawString(text, x, y);
        g2().setColor(oldColor);
    }

    /**
     * Draws a circle<br>
     *
     * @param dim   circle dimension
     * @param color circle color
     */
    public void drawCircle(Dim dim, Color color) {
        g2().setColor(color);
        oval(dim, true);
    }

    /**
     * Draws a circle border<br>
     *
     * @param dim   circle dimension
     * @param color border color
     */
    public void drawCircleBorder(Dim dim, Color color) {
        g2().setColor(color);
        oval(dim, false);
    }

    /**
     * Draws a rounded rectangle<br>
     *
     * @param dim rectangle dimension
     * @param arc rectangle curvature
     */
    public void drawRoundRect(Dim dim, int arc) {
        roundRect(dim, arc, true);
    }

    /**
     * Draws a rounded rectangle with the specified color<br>
     *
     * @param dim   rectangle dimension
     * @param arc   rectangle curvature
     * @param color rectangle color
     */
    public void drawRoundRect(Dim dim, int arc, Color color) {
        g2().setColor(color);
        roundRect(dim, arc, true);
    }

    /**
     * Draws a rounded rectangle border<br>
     *
     * @param dim rectangle dimension
     * @param arc rectangle curvature
     */
    public void drawRoundRectBorder(Dim dim, int arc) {
        roundRect(dim, arc, false);
    }

    /**
     * Draws a rounded rectangle border with the specified color<br>
     *
     * @param dim   rectangle dimension
     * @param arc   rectangle curvature
     * @param color border color
     */
    public void drawRoundRectBorder(Dim dim, double arc, Color color) {
        g2().setColor(color);
        roundRect(dim, arc, false);
    }

    /**
     * Draws a rounded rectangle<br>
     *
     * @param dim  rectangle dimension
     * @param arc  rectangle curvature
     * @param fill if true fills the rectangle, otherwise draws only the border
     */
    private void roundRect(Dim dim, double arc, boolean fill) {
        int x = getX(dim.x());
        int y = getY(dim.y());
        int w = getX(dim.width());
        int h = getY(dim.height());
        if (fill)
            g2().fillRoundRect(x, y, w, h, getX(arc), getY(arc));
        else
            g2().drawRoundRect(x, y, w, h, getX(arc), getY(arc));
    }

    /**
     * Draws a rectangle<br>
     *
     * @param dim   rectangle dimension
     * @param color rectangle color
     */
    public void drawRect(Dim dim, Color color) {
        var old = g2().getColor();
        g2().setColor(color);
        rect(dim, true);
        g2().setColor(old);
    }

    /**
     * Draws a rectangle border<br>
     *
     * @param dim   rectangle dimension
     * @param color border color
     */
    public void drawRectBorder(Dim dim, Color color) {
        var old = g2().getColor();
        g2().setColor(color);
        rect(dim, false);
        g2().setColor(old);
    }

    private void oval(Dim dim, boolean fill) {
        int x = getX(dim.x());
        int y = getY(dim.y());
        int w = getX(dim.width());
        int h = getY(dim.height());
        if (fill)
            g2().fillOval(x, y, w, h);
        else
            g2().drawOval(x, y, w, h);
    }


    private void rect(Dim dim, boolean fill) {
        int x = getX(dim.x());
        int y = getY(dim.y());
        int w = getX(dim.width());
        int h = getY(dim.height());
        if (fill)
            g2().fillRect(x, y, w, h);
        else
            g2().drawRect(x, y, w, h);
    }

    /**
     * Draws a line<br>
     *
     * @param x  starting x
     * @param y  starting y
     * @param x2 ending x
     * @param y2 ending y
     */
    public void line(double x, double y, double x2, double y2) {
        g2().drawLine(getX(x), getY(y), getX(x2), getY(y2));
    }

    /**
     * Draws a line with the specified color<br>
     *
     * @param x     starting x
     * @param y     starting y
     * @param x2    ending x
     * @param y2    ending y
     * @param color line color
     */
    public void line(double x, double y, double x2, double y2, Color color) {
        g2().setColor(color);
        line(x, y, x2, y2);
    }

    public int getX(double x) {
        return (int) (page.getWidth() * (x / 100f));
    }

    public static int getX(double x, Component component) {
        return (int) (component.getWidth() * (x / 100f));
    }

    public int getY(double y) {
        return (int) (page.getHeight() * (y / 100f));
    }

    public static int getY(double y, Component component) {
        return (int) (component.getHeight() * (y / 100f));
    }
}