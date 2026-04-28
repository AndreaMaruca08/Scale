package core.readycomponents;

import core.components.CachedScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

/**
 * <h1>CachedCoolBorder</h1>
 * <p>
 * A cached border component that draws a rounded rectangle with a shadow effect.
 * Extends {@link CachedScaleComponent} for automatic caching support.
 * </p>
 * <p>
 * Useful for static borders that don't change frequently.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class CachedCoolBorder extends CachedScaleComponent {
    private Color color;
    private Color shadowColor;
    private double arc;
    private double shadowOffset;

    public CachedCoolBorder(Dim dimensionToCover, Color color, double arc) {
        super(dimensionToCover);
        this.color = color;
        this.shadowColor = new Color(0, 0, 0, 50);
        this.arc = arc;
        this.shadowOffset = 0.15;
    }

    public CachedCoolBorder(Dim dimensionToCover, double arc) {
        this(dimensionToCover, new Color(70, 130, 180), arc);
    }

    @Override
    protected void drawComponent(ScaleGraphic g) {
        Dim shadowDim = dim.smaller(-shadowOffset, -shadowOffset);
        g.changeDrawWidth(0.3);
        g.drawRoundRectBorder(shadowDim, arc, shadowColor);
        g.changeDrawWidth(0.25);
        g.drawRoundRectBorder(dim, arc, color);

        g.changeDrawWidth(0.1);
        g.drawRoundRectBorder(dim.smaller(0.8, 0.8), arc, new Color(255, 255, 255, 100));
    }

    public void setArc(double arc) {
        this.arc = arc;
        invalidateCache();
    }

    public void setColor(Color color) {
        this.color = color;
        invalidateCache();
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
        invalidateCache();
    }

    public void setShadowOffset(double shadowOffset) {
        this.shadowOffset = shadowOffset;
        invalidateCache();
    }

    public Color getColor() {
        return color;
    }

    public double getArc() {
        return arc;
    }
}

