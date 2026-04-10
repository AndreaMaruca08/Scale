package test;

import core.ScaleComponent;
import core.ScalePage;
import core.ScaleUIApplication;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

public class TestPage extends ScalePage {

    public TestPage(ScaleUIApplication app) {
        super(app, "TestPage");
        setBackground(Color.black);

        TestButton button = new TestButton(new Dim(10, 10, 30, 10), "Test Button");

        ScaleComponent rect = new ScaleComponent(new Dim(40, 40, 30, 10)) {
            @Override
            public void draw(ScaleGraphic g) {
                g.drawRoundRect(dim, 10, Color.white);

            }
        };

        addScale(button);
        addScale(rect);
    }

    @Override
    public void draw(ScaleGraphic g) {}
}
