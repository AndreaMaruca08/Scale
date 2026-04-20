package core.components;

import core.utilities.ScaleGraphic;

/**
 * <h1>Interface for drawable components</h1>
 * @author Andrea Maruca
 * @since 1.0
 */
@FunctionalInterface
public interface Drawable {
    void draw(ScaleGraphic g);
}
