package test;

import core.ScaleUIApplication;
import core.game2d.CameraPage;
import core.readycomponents.CachedScaleLabel;
import core.readycomponents.CoolBorder;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;

/**
 * <h1>TestCameraSystem</h1>
 * <p>
 * Demonstrates the camera system with top-down movement.
 * Use WASD or Arrow Keys to move the camera around.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class TestCameraSystem {

    public static void main(String[] args) {
        ScaleUIApplication app = new ScaleUIApplication("Camera System Test", false);

        CameraTestPage page = new CameraTestPage(app);
        app.addPage(page);
        app.changePage("CameraTest");
    }

    static class CameraTestPage extends CameraPage {
        private int clickCount = 0;

        protected CameraTestPage(ScaleUIApplication app) {
            super(app, "CameraTest", 1000);

            // Ensure background is drawn correctly
            setBackground(new Color(50, 100, 50));
            setOpaque(true);

            // Set camera boundaries (prevent going too far)
            setCameraBounds(-400, 400, -400, 400);

            // Create grid elements
            createGrid();

            // Setup game cycle for continuous updates
            // Continuously repaint to show camera movement
            setupGameCycle(this::repaint);
            startGameCycle();
        }

        /**
         * Creates a grid of elements to demonstrate camera movement.
         */
        private void createGrid() {
            Color[] colors = {
                    new Color(70, 130, 180),
                    new Color(46, 139, 87),
                    new Color(184, 134, 11),
                    new Color(220, 20, 60)
            };

            // Create elements in a grid pattern
            // These will all move with the camera
            int cols = 5;
            int rows = 5;
            double elementSize = 18;
            double spacing = 20;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    double x = col * spacing;
                    double y = row * spacing;

                    CoolBorder border = new CoolBorder(
                            new Dim(x, y, elementSize, elementSize),
                            colors[(row + col) % colors.length],
                            1.0
                    );
                    border.setName("Element_" + row + "_" + col);
                    addScale(border);

                    // Add a label in the center
                    CachedScaleLabel label = new CachedScaleLabel(
                            new Dim(x + 2, y + 7, elementSize - 4, 5),
                            (row * cols + col + 1) + "",
                            Color.WHITE
                    );
                    addScale(label);
                }
            }

            // Add a center marker
            CoolBorder center = new CoolBorder(
                    new Dim(47.5, 47.5, 5, 5),
                    new Color(255, 255, 0),
                    0.5
            );
            center.setName("Center");
            addScale(center);
        }

        @Override
        public void draw(ScaleGraphic g) {

        }

        @Override
        protected void drawHUD(ScaleGraphic g) {
            // Draw camera info on top (HUD - not affected by camera, stays on screen)
            g.drawRect(new Dim(1, 1, 98, 8), new Color(0, 0, 0, 200));
            String camInfo = String.format("Camera: (%.0f, %.0f) | Speed: %.0f | Keys: WASD/Arrows to move",
                    getCameraX(), getCameraY(), getCamera().getSpeed());
            g.drawTextLeft(new Dim(2, 2, 96, 5), camInfo, Color.WHITE);
        }
    }
}


