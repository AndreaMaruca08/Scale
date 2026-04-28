package core.game2d;

import core.components.ScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;

/**
 * <h1>CachedScale2DGrid</h1>
 * <p>
 * An enhanced version of {@link Scale2DGrid} with additional cache management features.
 * Provides methods to control tile caching behavior and retrieve cache statistics.
 * </p>
 * <p>
 * This class extends the base functionality with:
 * - Explicit cache invalidation
 * - Memory usage monitoring
 * - Optional tile caching control
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class CachedScale2DGrid extends ScaleComponent {
    private final int cols;
    private final int rows;
    private final double cellWidthPct;
    private final double cellHeightPct;

    private final TilePainter tilePainter;
    private final CellRenderer cellRenderer;

    private BufferedImage cachedTile;
    private int cachedTileW = -1;
    private int cachedTileH = -1;

    private BufferedImage gridBuffer;
    private int bufferW = -1;
    private int bufferH = -1;

    private boolean tileCacheEnabled = true;
    private boolean gridCacheEnabled = true;

    public CachedScale2DGrid(Dim dim,
                             int cols,
                             int rows,
                             double cellWidthPct,
                             double cellHeightPct,
                             TilePainter tilePainter,
                             CellRenderer cellRenderer) {
        super(dim);
        this.cols = cols;
        this.rows = rows;
        this.cellWidthPct = cellWidthPct;
        this.cellHeightPct = cellHeightPct;
        this.tilePainter = tilePainter;
        this.cellRenderer = cellRenderer;
    }

    @Override
    public void draw(ScaleGraphic g) {
        draw(g, 0.0);
    }

    public void draw(ScaleGraphic g, double cameraOffsetPct) {
        int tw = g.getX(cellWidthPct);
        int th = g.getY(cellHeightPct);
        if (tw <= 0 || th <= 0) return;

        Graphics2D g2 = g.g2();

        if (tileCacheEnabled) {
            if (cachedTile == null || tw != cachedTileW || th != cachedTileH) {
                rebuildTile(tw, th, g2.getDeviceConfiguration());
                cachedTileW = tw;
                cachedTileH = th;
            }
        }

        int gridWpx = g.getX(dim.width());
        int gridHpx = g.getY(dim.height());

        if (gridCacheEnabled) {
            if (gridBuffer == null || gridWpx != bufferW || gridHpx != bufferH) {
                gridBuffer = createCompatible(gridWpx, gridHpx, g2.getDeviceConfiguration());
                bufferW = gridWpx;
                bufferH = gridHpx;
            }
        } else {
            // No grid buffering, draw directly
            gridBuffer = null;
        }

        int cameraOffsetPx = g.getX(cameraOffsetPct);

        if (gridBuffer != null) {
            Graphics2D bg = gridBuffer.createGraphics();
            try {
                bg.setComposite(AlphaComposite.Clear);
                bg.fillRect(0, 0, bufferW, bufferH);
                bg.setComposite(AlphaComposite.SrcOver);
                drawGridContent(bg, tw, th, cameraOffsetPx);
            } finally {
                bg.dispose();
            }

            g2.drawImage(gridBuffer,
                    g.getX(dim.x()),
                    g.getY(dim.y()),
                    null);
        } else {
            // Direct drawing without grid buffer
            drawGridContent(g2, tw, th, cameraOffsetPx);
        }
    }

    private void drawGridContent(Graphics2D g, int tw, int th, int cameraOffsetPx) {
        int bufferW = g.getClipBounds() != null ? g.getClipBounds().width : 1000;
        int bufferH = g.getClipBounds() != null ? g.getClipBounds().height : 1000;

        int firstCol = Math.max(0, -cameraOffsetPx / tw - 1);
        int lastCol = Math.min(cols, firstCol + bufferW / tw + 3);
        int visRows = Math.min(rows, bufferH / th + 2);

        for (int row = 0; row < visRows; row++) {
            for (int col = firstCol; col < lastCol; col++) {
                int px = col * tw + cameraOffsetPx;
                int py = row * th;

                if (tileCacheEnabled && cachedTile != null) {
                    g.drawImage(cachedTile, px, py, null);
                }

                if (cellRenderer != null) {
                    cellRenderer.render(g, col, row, px, py, tw, th);
                }
            }
        }
    }

    private BufferedImage createCompatible(int w, int h, GraphicsConfiguration gc) {
        if (gc != null) {
            return gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        }
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    private void rebuildTile(int tw, int th, GraphicsConfiguration gc) {
        cachedTile = createCompatible(tw, th, gc);
        Graphics2D ig = cachedTile.createGraphics();
        try {
            tilePainter.paint(ig, tw, th);
        } finally {
            ig.dispose();
        }
    }

    /**
     * Enables or disables tile caching.
     *
     * @param enabled true to enable tile caching
     */
    public void setTileCacheEnabled(boolean enabled) {
        this.tileCacheEnabled = enabled;
        if (!enabled) {
            clearTileCache();
        }
    }

    /**
     * Enables or disables grid buffer caching.
     *
     * @param enabled true to enable grid buffer caching
     */
    public void setGridCacheEnabled(boolean enabled) {
        this.gridCacheEnabled = enabled;
        if (!enabled) {
            clearGridCache();
        }
    }

    /**
     * Clears the tile cache.
     */
    public void clearTileCache() {
        if (cachedTile != null) {
            cachedTile.flush();
            cachedTile = null;
        }
        cachedTileW = -1;
        cachedTileH = -1;
    }

    /**
     * Clears the grid buffer cache.
     */
    public void clearGridCache() {
        if (gridBuffer != null) {
            gridBuffer.flush();
            gridBuffer = null;
        }
        bufferW = -1;
        bufferH = -1;
    }

    /**
     * Clears all caches.
     */
    public void clearAllCaches() {
        clearTileCache();
        clearGridCache();
    }

    /**
     * Gets the estimated memory usage of caches in bytes.
     *
     * @return estimated bytes used by caches
     */
    public long getCacheMemoryUsage() {
        long usage = 0;
        if (cachedTile != null) {
            usage += (long) cachedTile.getWidth() * cachedTile.getHeight() * 4;
        }
        if (gridBuffer != null) {
            usage += (long) gridBuffer.getWidth() * gridBuffer.getHeight() * 4;
        }
        return usage;
    }

    /**
     * Checks if tile caching is enabled.
     *
     * @return true if tile caching is enabled
     */
    public boolean isTileCacheEnabled() {
        return tileCacheEnabled;
    }

    /**
     * Checks if grid buffer caching is enabled.
     *
     * @return true if grid buffer caching is enabled
     */
    public boolean isGridCacheEnabled() {
        return gridCacheEnabled;
    }
}

