package core.readycomponents;

import core.components.ScalePressableComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

/**
 * <h1>Simple button</h1>
 * <p>Simple button with text and background color, an implementation of {@link ScalePressableComponent}</p>
 * @author Andrea Maruca
 * @since 1.1
 */
public class ScaleButton extends ScalePressableComponent {
    private String text;
    private boolean rounded;
    private Color txt;
    private Color bg;
    private Runnable action;

    public ScaleButton(Dim dim) {
        this(dim, "", true, Color.white, Color.black);
    }

    public ScaleButton(Dim dim, String text, Color color) {
        this(dim, text, true, color, Color.black);
    }

    public ScaleButton(Dim dim, String text, Color backgroundColor, Color textColor) {
        this(dim, text, true, backgroundColor, textColor);
    }

    public ScaleButton(Dim dim, String text, boolean rounded, Color backgroundColor, Color textColor) {
        super(dim);
        this.text = text;
        this.rounded = rounded;
        this.txt = textColor;
        this.bg = backgroundColor;
    }

    public boolean isRounded() {
        return rounded;
    }

    public void setRounded(boolean rounded) {
        this.rounded = rounded;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(Color textColor) {
        this.txt = textColor;
    }

    public void setBackground(Color bg) {
        this.bg = bg;
    }

    public void setTxtColor(Color txtColor) {
        this.txt = txtColor;
    }

    @Override
    public void draw(ScaleGraphic g) {
        if(rounded){
            g.drawRoundRect(dim, 10, bg);
        }else
            g.drawRect(dim, bg);
        g.drawText(dim, text, txt);
    }

    public void setAction(Runnable action){
        this.action = action;
    }

    @Override
    public void press() {
        if (action != null) {
            action.run();
        }
    }
}
