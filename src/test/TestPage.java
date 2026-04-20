package test;

import core.components.ScalePage;
import core.ScaleUIApplication;
import core.game2d.Scale2DGrid;
import core.utilities.Dim;

import java.awt.*;

public class TestPage extends ScalePage {

    private final Scale2DGrid grid;
    private double cameraOffset = 0;

    public TestPage(ScaleUIApplication app) {
        super(app, "TestPage");
        setBackground(Color.darkGray);

        grid = new Scale2DGrid(
                new Dim(0, 0, 100, 100),  // occupa tutta la pagina
                8,                        // 20 colonne
                10,                        // 10 righe
                10.0,                      // ogni cella è larga il 10% della pagina
                10.0,                      // ogni cella è alta il 10% della pagina
                (ig, tw, th) -> {
                    // sfondo della cella
                    ig.setColor(new Color(40, 40, 40));
                    ig.fillRect(0, 0, tw, th);
                    // bordo
                    ig.setColor(new Color(80, 80, 80));
                    ig.setStroke(new BasicStroke(1f));
                    ig.drawRect(0, 0, tw - 1, th - 1);
                },
                (g2, col, row, px, py, tw, th) -> {
                    // disegna un cerchio rosso nella cella (2,3)
                    if (col == 7  && row == 3) {
                        g2.setColor(Color.red);
                        g2.fillOval(px + tw / 4, py + th / 4, tw / 2, th / 2);
                    }
                    // disegna un quadrato verde nella cella (5,1)
                    if (col == 5 && row == 1) {
                        g2.setColor(Color.green);
                        g2.fillRect(px + tw / 4, py + th / 4, tw / 2, th / 2);
                    }
                }
        );

        addScale(grid);

        // sposta la camera verso destra nel tempo
        setupGameCycle(() -> {
            cameraOffset -= 0.05;
        });
        startGameCycle();
    }

    @Override
    public void draw(core.utilities.ScaleGraphic g) {
        grid.draw(g, cameraOffset);
    }
}