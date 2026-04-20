package core.game2d;

import core.components.ScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <h1>Scale2DGrid</h1>
 * <p>
 * A generic cached-tile grid renderer. Draws a 2D grid within a given {@link Dim},
 * with an optional camera offset on the X axis for scrolling.
 * The tile image is cached and rebuilt only when the pixel size changes.
 * Cell content is delegated to a {@link CellRenderer} functional interface.
 * </p>
 *
 * @since 1.4
 * @author Andrea Maruca
 */
public class Scale2DGrid extends ScaleComponent {
    private final int cols;
    private final int rows;
    private final double cellWidthPct;
    private final double cellHeightPct;

    private final TilePainter tilePainter;
    private final CellRenderer cellRenderer;

    private BufferedImage cachedTile;
    private int cachedTileW = -1;
    private int cachedTileH = -1;

    /**
     * @param dim           bounding box of the grid (in % of page)
     * @param cols          number of columns
     * @param rows          number of rows
     * @param cellWidthPct  width of a single cell (in % of page width)
     * @param cellHeightPct height of a single cell (in % of page height)
     * @param tilePainter   paints the cached background tile image
     * @param cellRenderer  renders additional content per cell (obstacles, sprites, etc.)
     */
    public Scale2DGrid(Dim dim,
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

    /**
     * Draws the grid with no camera offset.
     */
    @Override
    public void draw(ScaleGraphic g) {
        draw(g, 0.0);
    }

    /**
     * Draws the grid with a horizontal camera offset.
     *
     * @param g               the graphic context
     * @param cameraOffsetPct horizontal offset in % of page width (positive = scroll right)
     */
    public void draw(ScaleGraphic g, double cameraOffsetPct) {
        int tw = g.getX(cellWidthPct);
        int th = g.getY(cellHeightPct);
        if (tw <= 0 || th <= 0) return;

        if (cachedTile == null || tw != cachedTileW || th != cachedTileH) {
            rebuildTile(tw, th);
            cachedTileW = tw;
            cachedTileH = th;
        }

        int cameraOffsetPx = g.getX(cameraOffsetPct);

        int originX = g.getX(dim.x()) + cameraOffsetPx;
        int originY = g.getY(dim.y());
        int gridWpx = g.getX(dim.width());
        int gridHpx = g.getY(dim.height());

        Graphics2D g2 = g.g2();
        Shape oldClip = g2.getClip();
        g2.clipRect(g.getX(dim.x()), originY, gridWpx, gridHpx);

        int firstCol = Math.max(0, -cameraOffsetPx / tw - 1);
        int lastCol  = Math.min(cols, firstCol + gridWpx / tw + 3);
        int visRows  = Math.min(rows, gridHpx / th + 2);

        for (int row = 0; row < visRows; row++) {
            for (int col = firstCol; col < lastCol; col++) {
                int px = originX + col * tw;
                int py = originY + row * th;

                g2.drawImage(cachedTile, px, py, null);

                if (cellRenderer != null) {
                    cellRenderer.render(g2, col, row, px, py, tw, th);
                }
            }
        }

        g2.setClip(oldClip);
    }

    /**
     * Forces the cached tile to be rebuilt on the next draw call.
     */
    public void invalidateTile() {
        cachedTile = null;
        cachedTileW = -1;
        cachedTileH = -1;
    }

    private void rebuildTile(int tw, int th) {
        cachedTile = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig = cachedTile.createGraphics();
        try {
            tilePainter.paint(ig, tw, th);
        } finally {
            ig.dispose();
        }
    }
}