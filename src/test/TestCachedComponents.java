package test;

import core.ScaleUIApplication;
import core.components.ScalePage;
import core.readycomponents.CachedScaleButton;
import core.readycomponents.CachedScaleLabel;
import core.readycomponents.CachedCoolBorder;
import core.utilities.CacheManager;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>TestCachedComponents</h1>
 * <p>
 * Demonstrates the usage of cached components from the new caching system.
 * Shows how to create, configure, and manage cached components.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class TestCachedComponents {

    public static void main(String[] args) {
        // Create application
        ScaleUIApplication app = new ScaleUIApplication("Cached Components Test",false);

        // Create and add page
        CachedComponentsPage page = new CachedComponentsPage(app);
        app.addPage(page);
        app.changePage("CachedTest");
    }

    static class CachedComponentsPage extends ScalePage {
        private CachedScaleLabel statsLabel;
        private int clickCount = 0;

        protected CachedComponentsPage(ScaleUIApplication app) {
            super(app, "CachedTest");

            // Ensure background is drawn
            setBackground(new Color(245, 245, 245));
            setOpaque(true);

            // Enable stats tracking
            CacheManager.enableStats();

            // Create a title
            CachedScaleLabel title = new CachedScaleLabel(
                    new Dim(10, 5, 80, 10),
                    "Cached Components Demo",
                    Color.BLACK
            );
            addScale(title);

            // Create cached buttons in a grid
            createButtonGrid();

            // Create border around section
            CachedCoolBorder border = new CachedCoolBorder(
                    new Dim(5, 20, 90, 50),
                    Color.BLUE,
                    2.0
            );
            addScale(border);

            // Create info label
            statsLabel = new CachedScaleLabel(
                    new Dim(10, 75, 80, 20),
                    "Click buttons to see cache in action!",
                    Color.DARK_GRAY
            );
            addScale(statsLabel);

            // Create toggle cache button
            CachedScaleButton toggleCacheButton = new CachedScaleButton(
                    new Dim(10, 95, 30, 5),
                    "Toggle Cache",
                    Color.GREEN,
                    Color.WHITE
            );
            toggleCacheButton.setAction(this::toggleCache);
            addScale(toggleCacheButton);

            // Create stats button
            CachedScaleButton statsButton = new CachedScaleButton(
                    new Dim(45, 95, 30, 5),
                    "Print Stats",
                    Color.ORANGE,
                    Color.WHITE
            );
            statsButton.setAction(() -> {
                printStats();
                setBackground(Color.RED);
            });
            addScale(statsButton);
        }

        private void createButtonGrid() {
            String[] labels = {"Button 1", "Button 2", "Button 3", "Button 4"};
            Color[] colors = {
                    new Color(70, 130, 180),  // Steel Blue
                    new Color(46, 139, 87),   // Sea Green
                    new Color(184, 134, 11),  // Dark Goldenrod
                    new Color(220, 20, 60)    // Crimson
            };

            int cols = 2;
            int rows = 2;
            double startX = 10;
            double startY = 25;
            double buttonWidth = 35;
            double buttonHeight = 20;
            double spacingX = 40;
            double spacingY = 22;

            int index = 0;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    double x = startX + col * spacingX;
                    double y = startY + row * spacingY;

                    CachedScaleButton button = new CachedScaleButton(
                            new Dim(x, y, buttonWidth, buttonHeight),
                            labels[index],
                            colors[index],
                            Color.WHITE
                    );

                    final int buttonIndex = index;
                    button.setAction(() -> onButtonPressed(buttonIndex));

                    // Register with CacheManager
                    CacheManager.register(button);

                    addScale(button);
                    index++;
                }
            }
        }

        private void onButtonPressed(int buttonIndex) {
            clickCount++;
            String message = "Button " + (buttonIndex + 1) + " pressed! (Total clicks: " + clickCount + ")";
            statsLabel.setText(message);
            update(statsLabel.getDim());

            System.out.println(message);
        }

        private void toggleCache() {
            boolean currentState = CacheManager.getRegisteredComponentCount() > 0;
            CacheManager.setGlobalCacheEnabled(!currentState);

            String status = currentState ? "disabled" : "enabled";
            System.out.println("Global cache " + status);
            System.out.println("Registered components: " + CacheManager.getRegisteredComponentCount());
        }

        private void printStats() {
            CacheManager.printStats();
            System.out.println("Total button clicks: " + clickCount);
            System.out.println("---");
        }

        @Override
        public void draw(ScaleGraphic g) {

            // Draw title background
            g.drawRect(new Dim(5, 2, 90, 12), new Color(100, 150, 200));
        }
    }
}

