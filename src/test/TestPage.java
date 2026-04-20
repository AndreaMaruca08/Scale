
package test;

import core.readycomponents.CoolBorder;
import core.readycomponents.ScaleButton;
import core.components.ScalePage;
import core.ScaleUIApplication;
import core.readycomponents.ScaleLabel;
import core.utilities.Dim;

import javax.swing.*;
import java.awt.*;

public class TestPage extends ScalePage {

    public TestPage(ScaleUIApplication app) {
        super(app, "TestPage");
        setBackground(Color.white);


        TestButton button = new TestButton(new Dim(10, 10, 30, 10), "Test Button");

        ScaleButton btn = new ScaleButton(new Dim(10, 30, 30, 10), "Scale Button", Color.blue, Color.white);
        btn.setAction(() -> {
            btn.setBackground(Color.red);
            btn.setTextColor(Color.white);
        });

        ScaleLabel label = new ScaleLabel(new Dim(10, 50, 30, 10), "Scale Label");
        CoolBorder border = new CoolBorder(label.getDim(), Color.darkGray, 2);

        addScale(border);
        addScale(button);
        addScale(btn);
        addScale(label);

        setupGameCycle(this::actio);
        startGameCycle();

    }
    public void actio(){
        repaint();
    }
}