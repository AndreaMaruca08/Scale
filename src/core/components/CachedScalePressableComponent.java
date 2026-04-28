package core.components;

import core.utilities.Dim;

import java.awt.*;

/**
 * <h1>CachedScalePressableComponent</h1>
 * <p>
 * An abstract base class for pressable components that support optional caching.
 * Combines caching capabilities with the ability to respond to user input.
 * </p>
 * <p>
 * Useful for interactive elements like buttons that are expensive to render
 * but infrequently updated.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public abstract class CachedScalePressableComponent extends CachedScaleComponent implements Pressable {
    /**
     * Creates a cached pressable component.
     *
     * @param dim the dimension of the component
     */
    public CachedScalePressableComponent(Dim dim) {
        super(dim);
    }

    /**
     * Creates a cached pressable component.
     *
     * @param dim  the dimension of the component
     * @param name the name of the component
     */
    public CachedScalePressableComponent(Dim dim, String name) {
        super(dim, name);
    }

    @Override
    public boolean checkPress(double x, double y, Component parent) {
        return dim.hasPoint(x, y, parent);
    }
}

