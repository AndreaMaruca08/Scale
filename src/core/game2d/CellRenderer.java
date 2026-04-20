package core.game2d;

import java.awt.*;

/**
 * Functional interface to render a cell.
 *
 * @since 1.4
 * @author Andrea Maruca
 */
@FunctionalInterface
public interface CellRenderer {
    /**
     * Called for each visible cell.
     *
     * @param g2   the raw Graphics2D context (already translated to the grid origin + camera)
     * @param col  grid column index
     * @param row  grid row index
     * @param px   top-left pixel X of the cell
     * @param py   top-left pixel Y of the cell
     * @param tw   tile width in pixels
     * @param th   tile height in pixels
     */
    void render(Graphics2D g2, int col, int row, int px, int py, int tw, int th);
}