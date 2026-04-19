package core.utilities;

/**
 * <h1>Manage the background scrolling in the X dimension</h1>
 * @since 1.3
 * @author Andrea Maruca
 */
public class BackgroundController {
    protected float offsetX = 0;
    protected float offsetY = 0;
    protected final float velocityX;
    protected final boolean wrap;

    public BackgroundController(float velocityX, boolean wrap) {
        this.velocityX = velocityX;
        this.wrap = wrap;
    }

    public BackgroundController(float velocityX) {
        this(velocityX, false);
    }

    public void update(float deltaTime, ScaleGraphic g) {
        offsetX += velocityX * deltaTime;

        if (wrap) {
            int width = g.getX(100);
            if (width > 0) {
                offsetX = ((offsetX % width) + width) % width;
            }
        }
    }

    public float getOffsetX() { return offsetX; }
    public float getOffsetY() { return offsetY; }
}