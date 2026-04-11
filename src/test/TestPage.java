package test;

import core.readycomponents.CoolBorder;
import core.readycomponents.ScaleButton;
import core.components.ScalePage;
import core.ScaleUIApplication;
import core.readycomponents.ScaleLabel;
import core.readycomponents.ScaleTxtArea;
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

        ScaleTxtArea txtArea = new ScaleTxtArea(new Dim(10, 70, 30, 20), Color.green, Color.white);

        CoolBorder border = new CoolBorder(label.getDim(), Color.darkGray, 2);

        createKey("test",
                () -> JOptionPane.showMessageDialog(null, "Key pressed! " + txtArea.getText()),
                "CTRL", "T");



        addScale(border);
        addScale(txtArea);
        addScale(button);
        addScale(btn);
        addScale(label);
    }
}
