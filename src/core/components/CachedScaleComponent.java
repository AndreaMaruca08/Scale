package core.components;

import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <h1>CachedScaleComponent</h1>
 * <p>
 * An abstract base class for components that support optional caching.
 * Automatically caches the rendered content in a BufferedImage and reuses it
 * unless the cache is invalidated or dimensions change.
 * </p>
 * <p>
 * This class is useful for components that are expensive to render but don't
 * change frequently. The cache rebuilds only when necessary.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public abstract class CachedScaleComponent extends ScaleComponent {
    private BufferedImage cachedImage;
    private int cachedWidth = -1;
    private int cachedHeight = -1;
    private boolean cacheEnabled;
    private boolean cacheDirty;

    /**
     * Creates a cached component with caching initially enabled.
     *
     * @param dim the dimension of the component
     */
    public CachedScaleComponent(Dim dim) {
        super(dim);
        this.cacheEnabled = true;
        this.cacheDirty = true;
    }

    /**
     * Creates a cached component with caching initially enabled.
     *
     * @param dim  the dimension of the component
     * @param name the name of the component
     */
    public CachedScaleComponent(Dim dim, String name) {
        super(dim, name);
        this.cacheEnabled = true;
        this.cacheDirty = true;
    }

    /**
     * Draws the component with caching support.
     * If cache is enabled and valid, uses the cached image.
     * Otherwise, rebuilds the cache.
     *
     * @param g the ScaleGraphic context
     */
    @Override
    public final void draw(ScaleGraphic g) {
        if (!cacheEnabled) {
            drawComponent(g);
            return;
        }

        int pixelWidth = g.getX(dim.width());
        int pixelHeight = g.getY(dim.height());

        if (pixelWidth <= 0 || pixelHeight <= 0) return;

        // Rebuild cache if dimensions changed or cache is dirty
        if (cachedImage == null || pixelWidth != cachedWidth || pixelHeight != cachedHeight || cacheDirty) {
            rebuildCache(pixelWidth, pixelHeight, g.g2().getDeviceConfiguration());
            cachedWidth = pixelWidth;
            cachedHeight = pixelHeight;
            cacheDirty = false;
        }

        // Draw the cached image at the component position
        int x = g.getX(dim.x());
        int y = g.getY(dim.y());
        g.g2().drawImage(cachedImage, x, y, null);
    }

    /**
     * Rebuilds the cache by rendering the component to a BufferedImage.
     *
     * @param width       the width in pixels
     * @param height      the height in pixels
     * @param gc          the GraphicsConfiguration for compatibility
     */
    private void rebuildCache(int width, int height, GraphicsConfiguration gc) {
        cachedImage = createCompatibleImage(width, height, gc);
        Graphics2D g2 = cachedImage.createGraphics();
        try {
            // Enable anti-aliasing for smoother graphics
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Create a temporary ScaleGraphic with a mock page that has the exact cache dimensions
            ScaleGraphic tempGraphic = new ScaleGraphic(
                    (ScalePage) new MockPage(width, height),
                    g2
            );

            // Temporarily replace the dimension with a full-screen dimension for the cache
            Dim originalDim = dim;
            dim = new Dim(0, 0, 100, 100);

            try {
                drawComponent(tempGraphic);
            } finally {
                dim = originalDim;
            }
        } finally {
            g2.dispose();
        }
    }

    /**
     * Creates a compatible BufferedImage for this component.
     *
     * @param width  the width in pixels
     * @param height the height in pixels
     * @param gc     the GraphicsConfiguration
     * @return a compatible BufferedImage with alpha transparency
     */
    private BufferedImage createCompatibleImage(int width, int height, GraphicsConfiguration gc) {
        if (gc != null) {
            return gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        }
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Subclasses should override this method to implement the actual drawing logic.
     * This method is called both during caching and when caching is disabled.
     *
     * @param g the ScaleGraphic context
     */
    protected abstract void drawComponent(ScaleGraphic g);

    /**
     * Enables or disables caching for this component.
     *
     * @param enabled true to enable caching, false to disable
     */
    public void setCacheEnabled(boolean enabled) {
        this.cacheEnabled = enabled;
        if (!enabled) {
            clearCache();
        }
    }

    /**
     * Checks if caching is currently enabled.
     *
     * @return true if caching is enabled, false otherwise
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * Invalidates the current cache, forcing a rebuild on the next draw.
     */
    public void invalidateCache() {
        this.cacheDirty = true;
    }

    /**
     * Clears the cache immediately.
     */
    public void clearCache() {
        if (cachedImage != null) {
            cachedImage.flush();
            cachedImage = null;
        }
        cachedWidth = -1;
        cachedHeight = -1;
        cacheDirty = true;
    }

    /**
     * A mock page to provide width/height context during cache rendering.
     * This is a lightweight ScalePage for internal use.
     */
    public static class MockPage extends ScalePage {
        private final int width;
        private final int height;

        public MockPage(int width, int height) {
            super();  // Calls the protected no-arg constructor
            this.width = width;
            this.height = height;
            setSize(width, height);
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }
    }
}

