package core.readycomponents;

import core.components.ScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

/**
 * <h1>Simple label</h1>
 * <p>Simple label with text and foreground color, an implementation of {@link ScaleComponent}</p>
 * @author Andrea Maruca
 * @since 1.1
 */
public class ScaleLabel extends ScaleComponent{
    private String text;
    private Color textColor;

    public ScaleLabel(Dim dim, String text) {
        this(dim, text, Color.black);
    }
    public ScaleLabel(Dim dim, String text, Color textColor) {
        super(dim);
        this.text = text;
        this.textColor = textColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(ScaleGraphic g) {
        g.drawWrapTextWithColor(text, dim, textColor);
    }
}
