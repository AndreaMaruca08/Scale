package core.game2d;

import core.ScaleUIApplication;
import core.components.ScaleComponent;
import core.components.ScalePage;
import core.utilities.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * <h1>CameraPage</h1>
 * <p>
 * A page with a built-in top-down camera system that responds to WASD input.
 * Extends ScalePage to provide camera offset support with configurable movement speed and boundaries.
 * </p>
 * <p>
 * Features:
 * - WASD camera movement (W=up, A=left, S=down, D=right)
 * - Configurable movement speed
 * - Boundary limits (prevents camera from going off-map)
 * - Smooth movement based on delta time
 * - Optional multi-key support (simultaneous key pressing)
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public abstract class CameraPage extends ScalePage {
    protected final CameraController camera;
    protected final Set<Integer> pressedKeys;

    /**
     * Creates a camera page with default settings.
     *
     * @param app  the ScaleUIApplication
     * @param name the page name
     */
    public CameraPage(ScaleUIApplication app, String name) {
        super(app, name);
        this.camera = new CameraController(1050.0);
        this.pressedKeys = new HashSet<>();

        setupKeyInput();
    }

    /**
     * Creates a camera page with custom camera speed.
     *
     * @param app   the ScaleUIApplication
     * @param name  the page name
     * @param speed camera movement speed (units per frame)
     */
    public CameraPage(ScaleUIApplication app, String name, double speed) {
        super(app, name);
        this.camera = new CameraController(speed);
        this.pressedKeys = new HashSet<>();

        setupKeyInput();
    }

    /**
     * Sets up keyboard input handling for camera movement.
     */
    private void setupKeyInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                updateCameraVelocity();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
                updateCameraVelocity();
            }
        });

        setFocusable(true);
    }

    /**
     * Updates camera velocity based on currently pressed keys.
     * W/Up = move up, A/Left = move left, S/Down = move down, D/Right = move right
     */
    private void updateCameraVelocity() {
        double vx = 0;
        double vy = 0;

        if (pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_UP)) {
            vy -= 1;  // Move up (negative Y)
        }
        if (pressedKeys.contains(KeyEvent.VK_S) || pressedKeys.contains(KeyEvent.VK_DOWN)) {
            vy += 1;  // Move down (positive Y)
        }
        if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT)) {
            vx -= 1;  // Move left (negative X)
        }
        if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            vx += 1;  // Move right (positive X)
        }

        camera.setVelocity(vx, vy);
    }

    /**
     * Override paintComponent to apply camera offset.
     * All child components are translated by the camera offset.
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Update camera position
        camera.update(lastDeltaTime);

        // Draw background manually
        if (getBackground() != null) {
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Save graphics state
        var originalTransform = g2.getTransform();

        // Apply camera translation
        g2.translate(-camera.getOffsetX(), -camera.getOffsetY());

        // Create temp ScaleGraphic for world drawing
        ScaleGraphic worldGraphic = new ScaleGraphic(this, g2);
        draw(worldGraphic);

        // Draw world children
        for (ScaleComponent child : getChildren()) {
            child.draw(worldGraphic);
        }

        // Restore original transform for HUD
        g2.setTransform(originalTransform);

        // Draw HUD (not affected by camera)
        ScaleGraphic hudGraphic = new ScaleGraphic(this, g2);
        drawHUD(hudGraphic);
    }

    /**
     * Override this method to draw HUD elements that don't move with the camera.
     * Called after camera offset is reset, so coordinates are screen-space.
     *
     * @param g the ScaleGraphic context (unaffected by camera offset)
     */
    protected void drawHUD(ScaleGraphic g) {
        // Override to draw HUD elements
    }

    /**
     * Gets the camera controller for advanced configuration.
     *
     * @return the CameraController
     */
    public CameraController getCamera() {
        return camera;
    }

    /**
     * Sets the camera movement speed.
     *
     * @param speed speed in units per frame
     */
    public void setCameraSpeed(double speed) {
        camera.setSpeed(speed);
    }

    /**
     * Sets the camera movement boundaries.
     * Prevents the camera from moving beyond these limits.
     *
     * @param minX minimum X offset
     * @param maxX maximum X offset
     * @param minY minimum Y offset
     * @param maxY maximum Y offset
     */
    public void setCameraBounds(double minX, double maxX, double minY, double maxY) {
        camera.setBounds(minX, maxX, minY, maxY);
    }

    /**
     * Sets horizontal-only camera boundaries.
     *
     * @param min minimum X offset
     * @param max maximum X offset
     */
    public void setCameraHorizontalBounds(double min, double max) {
        camera.setHorizontalBounds(min, max);
    }

    /**
     * Sets vertical-only camera boundaries.
     *
     * @param min minimum Y offset
     * @param max maximum Y offset
     */
    public void setCameraVerticalBounds(double min, double max) {
        camera.setVerticalBounds(min, max);
    }

    /**
     * Resets the camera to the origin (0, 0).
     */
    public void resetCamera() {
        camera.reset();
    }

    /**
     * Sets the camera position directly.
     *
     * @param x X offset
     * @param y Y offset
     */
    public void setCameraPosition(double x, double y) {
        camera.setPosition(x, y);
    }

    /**
     * Gets the current camera X offset.
     *
     * @return offset X in pixels
     */
    public double getCameraX() {
        return camera.getOffsetX();
    }

    /**
     * Gets the current camera Y offset.
     *
     * @return offset Y in pixels
     */
    public double getCameraY() {
        return camera.getOffsetY();
    }
}

