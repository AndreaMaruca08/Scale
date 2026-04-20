package core.game2d;

import java.awt.*;

/**
 * Functional interface to paint the background of a single tile into a BufferedImage.
 * Used to build the cached tile once.
 *
 * @since 1.4
 * @author Andrea Maruca
 */
@FunctionalInterface
public interface TilePainter {
    /**
     * @param ig the Graphics2D of the tile image (origin is top-left of the tile)
     * @param tw tile width in pixels
     * @param th tile height in pixels
     */
    void paint(Graphics2D ig, int tw, int th);
}