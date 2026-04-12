package core.components;

import core.utilities.Dim;

/**
 * <h1>Abstract class for components that can be updated</h1>
 * @since 1.1.1
 * @author Andrea Maruca
 */
public abstract class ScaleUpdatableComponent extends ScaleComponent implements Updatable {
    private final ScalePage page;
    public ScaleUpdatableComponent(Dim dim, ScalePage page) {
        super(dim);
        this.page = page;
    }
    public ScaleUpdatableComponent(Dim dim, ScalePage page, String name) {
        super(dim, name);
        this.page = page;
    }
    public void updateAll() {
        page.repaint();
    }
    @Override
    public void update() {
        page.update(dim);
    }
}
