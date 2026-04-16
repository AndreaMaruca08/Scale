package core.readycomponents;

import core.components.ScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

/**
 * <h1>CoolBorder</h1>
 * <p>
 * A custom border component that draws a rounded rectangle with a shadow effect.
 * </p>
 */
public class CoolBorder extends ScaleComponent {
    private Color color;
    private Color shadowColor;
    private double arc;
    private double shadowOffset;

    public CoolBorder(Dim dimensionToCover, Color color, double arc) {
        super(dimensionToCover);
        this.color = color;
        this.shadowColor = new Color(0, 0, 0, 50);
        this.arc = arc;
        this.shadowOffset = 0.15;
    }

    public CoolBorder(Dim dimensionToCover, double arc) {
        this(dimensionToCover, new Color(70, 130, 180), arc);
    }

    public void setArc(double arc) {
        this.arc = arc;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }

    public void setShadowOffset(double shadowOffset) {
        this.shadowOffset = shadowOffset;
    }

    public Color getColor() {
        return color;
    }

    public double getArc() {
        return arc;
    }

    @Override
    public void draw(ScaleGraphic g) {
        Dim shadowDim = dim.smaller(-shadowOffset, -shadowOffset);
        g.changeDrawWidth(0.3);
        g.drawRoundRectBorder(shadowDim, arc, shadowColor);
        g.changeDrawWidth(0.25);
        g.drawRoundRectBorder(dim, arc, color);

        g.changeDrawWidth(0.1);
        g.drawRoundRectBorder(dim.smaller(0.8, 0.8), arc, new Color(255, 255, 255, 100));
    }
}