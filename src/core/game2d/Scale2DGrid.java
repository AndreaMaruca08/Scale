package core.game2d;

import core.components.ScaleComponent;
import core.utilities.Dim;
import core.utilities.ScaleGraphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;

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

    private BufferedImage gridBuffer;
    private int bufferW = -1;
    private int bufferH = -1;

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

    @Override
    public void draw(ScaleGraphic g) {
        draw(g, 0.0);
    }

    public void draw(ScaleGraphic g, double cameraOffsetPct) {
        int tw = g.getX(cellWidthPct);
        int th = g.getY(cellHeightPct);
        if (tw <= 0 || th <= 0) return;

        Graphics2D g2 = g.g2();

        if (cachedTile == null || tw != cachedTileW || th != cachedTileH) {
            rebuildTile(tw, th, g2.getDeviceConfiguration());
            cachedTileW = tw;
            cachedTileH = th;
        }

        int gridWpx = g.getX(dim.width());
        int gridHpx = g.getY(dim.height());

        if (gridBuffer == null || gridWpx != bufferW || gridHpx != bufferH) {
            gridBuffer = createCompatible(gridWpx, gridHpx, g2.getDeviceConfiguration());
            bufferW = gridWpx;
            bufferH = gridHpx;
        }

        int cameraOffsetPx = g.getX(cameraOffsetPct);

        Graphics2D bg = gridBuffer.createGraphics();
        try {
            bg.setComposite(AlphaComposite.Clear);
            bg.fillRect(0, 0, bufferW, bufferH);
            bg.setComposite(AlphaComposite.SrcOver);

            int firstCol = Math.max(0, -cameraOffsetPx / tw - 1);
            int lastCol  = Math.min(cols, firstCol + bufferW / tw + 3);
            int visRows  = Math.min(rows, bufferH / th + 2);

            for (int row = 0; row < visRows; row++) {
                for (int col = firstCol; col < lastCol; col++) {
                    int px = col * tw + cameraOffsetPx;
                    int py = row * th;

                    bg.drawImage(cachedTile, px, py, null);

                    if (cellRenderer != null) {
                        cellRenderer.render(bg, col, row, px, py, tw, th);
                    }
                }
            }

        } finally {
            bg.dispose();
        }

        // 🔴 UNA sola drawImage
        g2.drawImage(gridBuffer,
                g.getX(dim.x()),
                g.getY(dim.y()),
                null);
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
}