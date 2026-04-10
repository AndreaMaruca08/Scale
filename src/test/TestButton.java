package test;

import core.components.ScalePressableComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

public class TestButton extends ScalePressableComponent {
    private String text;

    private Color backgroundColor;
    private Color textColor;

    private boolean pressed = false;

    public TestButton(Dim dim, String text) {
        super(dim);
        this.text = text;
        backgroundColor = Color.white;
        textColor = Color.black;
    }

    @Override
    public void draw(ScaleGraphic g) {
        g.drawRoundRect(dim, 10, backgroundColor);
        g.setColor(textColor);
        g.drawText(dim, text, textColor);
    }

    @Override
    public void press() {
        pressed = !pressed;
        if (pressed) {
            backgroundColor = Color.white;
            textColor = Color.black;
        } else {
            backgroundColor = Color.gray;
            textColor = Color.white;
        }
    }
}
