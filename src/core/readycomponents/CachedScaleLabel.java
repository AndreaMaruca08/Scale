package core.readycomponents;

import core.components.CachedScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

/**
 * <h1>CachedScaleLabel</h1>
 * <p>
 * A label component with built-in caching capabilities.
 * Extends {@link CachedScaleComponent} to automatically cache rendering.
 * </p>
 * <p>
 * Useful for static labels that don't change frequently.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class CachedScaleLabel extends CachedScaleComponent {
    private String text;
    private Color textColor;

    public CachedScaleLabel(Dim dim, String text) {
        this(dim, text, Color.black);
    }

    public CachedScaleLabel(Dim dim, String text, Color textColor) {
        super(dim);
        this.text = text;
        this.textColor = textColor;
    }

    @Override
    protected void drawComponent(ScaleGraphic g) {
        g.drawWrapTextWithColor(text, dim, textColor);
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        invalidateCache();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidateCache();
    }
}

