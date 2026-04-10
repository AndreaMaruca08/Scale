package core.components;

import java.awt.*;

/**
 * <h1>Interface for pressable components</h1>
 * @author Andrea Maruca
 * @since 1.0
 */
public interface Pressable {
    boolean checkPress(double x, double y, Component parent);
    void press();
}
