package core.components;

import core.utilities.Dim;

/**
 * <h1>ScaleComponent</h1><br>
 * <p>The core of the graphics system. Provides common functionality and properties for graphics components.</p>
 * <p>This class is abstract and should not be instantiated directly.</p>
 * <p>It is used as a base class for all graphics components in the application.</p>
 * @author Andrea Maruca
 * @since 1.0
 */

public abstract class ScaleComponent implements Drawable {
    protected Dim dim;

    public ScaleComponent(Dim dim) {
        this.dim = dim;
    }

    public Dim getDim() {
        return dim;
    }

    public void setDim(Dim dim) {
        this.dim = dim;
    }
}