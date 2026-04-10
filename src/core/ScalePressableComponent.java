package core;

import core.utilities.Dim;

import java.awt.*;

/**
 * <h1>Abstract class for components that can be pressed and released</h1>
 *
 * @since 1.0
 * @author Andrea Maruca
 */
public abstract class ScalePressableComponent extends ScaleComponent implements Pressable{
    public ScalePressableComponent(Dim dim) {
        super(dim);
    }

    @Override
    public boolean checkPress(double x, double y, Component parent){
        return dim.hasPoint(x, y, parent);
    }
}
