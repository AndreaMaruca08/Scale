package core.readycomponents;

import core.components.ScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * <h1>Simple textArea</h1>
 * <p>Simple text area for input, an implementation of {@link ScaleComponent}</p>
 * @author Andrea Maruca
 * @since 1.1
 */
public class ScaleTxtArea extends ScaleComponent {
    private final JTextArea textArea;

    public ScaleTxtArea(Dim dim, Color backgroundColor, Color textColor) {
        super(dim);
        this.textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(backgroundColor);
        textArea.setForeground(textColor);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setFont(Font font){
        textArea.setFont(font);
    }

    public ScaleTxtArea(Dim dim) {
        this(dim, Color.white, Color.black);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
    }

    @Override
    public void draw(ScaleGraphic g) {
        textArea.setBounds(dim.toRectangleSmaller(g.page()));
        var compo = g.page().getComponents();
        if (!Arrays.asList(compo).contains(textArea)) {
            g.page().add(textArea);
        }
    }
}
