package core.utilities;

/**
 * <h1>CameraController</h1>
 * <p>
 * Manages camera offset and boundaries for top-down camera systems.
 * Provides smooth movement with configurable velocity and limits.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class CameraController {
    private double offsetX = 0;
    private double offsetY = 0;
    private double velocityX = 0;
    private double velocityY = 0;
    private double speed = 1.0;  // Units per frame
    private double minX = Double.NEGATIVE_INFINITY;
    private double maxX = Double.POSITIVE_INFINITY;
    private double minY = Double.NEGATIVE_INFINITY;
    private double maxY = Double.POSITIVE_INFINITY;

    /**
     * Creates a camera controller with default settings.
     */
    public CameraController() {
    }

    /**
     * Creates a camera controller with specified speed.
     *
     * @param speed movement speed in units per frame
     */
    public CameraController(double speed) {
        this.speed = speed;
    }

    /**
     * Updates camera position based on current velocity and delta time.
     *
     * @param deltaTime time elapsed since last frame in seconds
     */
    public void update(float deltaTime) {
        // Apply velocity
        offsetX += velocityX * speed * deltaTime;
        offsetY += velocityY * speed * deltaTime;

        // Apply boundaries
        offsetX = Math.max(minX, Math.min(maxX, offsetX));
        offsetY = Math.max(minY, Math.min(maxY, offsetY));
    }

    /**
     * Sets the movement input for this frame.
     * Values should be -1, 0, or 1 for each axis.
     *
     * @param vx horizontal velocity (-1, 0, or 1)
     * @param vy vertical velocity (-1, 0, or 1)
     */
    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    /**
     * Adds to the current movement velocity (for multi-key pressing).
     *
     * @param vx horizontal velocity delta
     * @param vy vertical velocity delta
     */
    public void addVelocity(double vx, double vy) {
        this.velocityX += vx;
        this.velocityY += vy;
        // Clamp to [-1, 1]
        this.velocityX = Math.max(-1, Math.min(1, velocityX));
        this.velocityY = Math.max(-1, Math.min(1, velocityY));
    }

    /**
     * Gets the current camera X offset.
     *
     * @return offset X in pixels
     */
    public double getOffsetX() {
        return offsetX;
    }

    /**
     * Gets the current camera Y offset.
     *
     * @return offset Y in pixels
     */
    public double getOffsetY() {
        return offsetY;
    }

    /**
     * Sets the camera position directly.
     *
     * @param x new X offset
     * @param y new Y offset
     */
    public void setPosition(double x, double y) {
        this.offsetX = Math.max(minX, Math.min(maxX, x));
        this.offsetY = Math.max(minY, Math.min(maxY, y));
    }

    /**
     * Gets the current movement speed.
     *
     * @return speed in units per frame
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the movement speed.
     *
     * @param speed speed in units per frame
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets the horizontal movement boundaries.
     *
     * @param min minimum X offset
     * @param max maximum X offset
     */
    public void setHorizontalBounds(double min, double max) {
        this.minX = min;
        this.maxX = max;
    }

    /**
     * Sets the vertical movement boundaries.
     *
     * @param min minimum Y offset
     * @param max maximum Y offset
     */
    public void setVerticalBounds(double min, double max) {
        this.minY = min;
        this.maxY = max;
    }

    /**
     * Sets the boundaries for both axes.
     *
     * @param minX minimum X offset
     * @param maxX maximum X offset
     * @param minY minimum Y offset
     * @param maxY maximum Y offset
     */
    public void setBounds(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Resets the camera to origin with no velocities.
     */
    public void reset() {
        this.offsetX = 0;
        this.offsetY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }

    /**
     * Clears movement input without affecting position.
     */
    public void clearVelocity() {
        this.velocityX = 0;
        this.velocityY = 0;
    }
}

