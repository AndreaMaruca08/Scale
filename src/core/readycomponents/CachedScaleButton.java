package core.readycomponents;

import core.components.CachedScalePressableComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

/**
 * <h1>CachedScaleButton</h1>
 * <p>
 * A button component with built-in caching capabilities.
 * Extends {@link CachedScalePressableComponent} to automatically cache rendering
 * while supporting user interaction.
 * </p>
 * <p>
 * Useful when the button appearance is static and doesn't change frequently.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class CachedScaleButton extends CachedScalePressableComponent {
    private String text;
    private boolean rounded;
    private Color txt;
    private Color bg;
    private Runnable action;

    public CachedScaleButton(Dim dim) {
        this(dim, "", true, Color.white, Color.black);
    }

    public CachedScaleButton(Dim dim, String text, Color color) {
        this(dim, text, true, color, Color.black);
    }

    public CachedScaleButton(Dim dim, String text, Color backgroundColor, Color textColor) {
        this(dim, text, true, backgroundColor, textColor);
    }

    public CachedScaleButton(Dim dim, String text, boolean rounded, Color backgroundColor, Color textColor) {
        super(dim);
        this.text = text;
        this.rounded = rounded;
        this.txt = textColor;
        this.bg = backgroundColor;
    }

    @Override
    protected void drawComponent(ScaleGraphic g) {
        if (rounded) {
            g.drawRoundRect(dim, 3, bg);
        } else {
            g.drawRect(dim, bg);
        }
        g.drawText(dim, text, txt);
    }

    public boolean isRounded() {
        return rounded;
    }

    public void setRounded(boolean rounded) {
        this.rounded = rounded;
        invalidateCache();
    }

    public void setText(String text) {
        this.text = text;
        invalidateCache();
    }

    public void setTextColor(Color textColor) {
        this.txt = textColor;
        invalidateCache();
    }

    public void setBackground(Color bg) {
        this.bg = bg;
        invalidateCache();
    }

    public void setTxtColor(Color txtColor) {
        this.txt = txtColor;
        invalidateCache();
    }

    public void setAction(Runnable action) {
        this.action = action;
    }


    @Override
    public void press() {
        if (action != null) {
            action.run();
        }
    }
}

