package core.utilities;

import java.awt.*;

/**
 * <h4>Representation of a graphic dimension from 0 to 100</h4>
 * <p>This record represents dimensions in an imaginary quantity that is transformed into pixels only at the end.
 * This ensures that the ratios between components remain identical regardless of resolution</p>
 *
 * @author Andrea Maruca
 * @since 1.0
 */
public record Dim(
        double x,
        double y,
        double width,
        double height
){

    public Dim lower(double howMuch){
        return new Dim(x, y + howMuch, width, height);
    }

    public Rectangle toRectangle(Component component) {
        return new Rectangle(
                ScaleGraphic.getX(x, component),
                ScaleGraphic.getY(y, component),
                ScaleGraphic.getX(width, component),
                ScaleGraphic.getY(height, component)
        );
    }
    public Rectangle toRectangleSmaller(Component component) {
        int dim = 2;
        return new Rectangle(
                ScaleGraphic.getX(x + dim, component),
                ScaleGraphic.getY(y + dim, component),
                ScaleGraphic.getX(width - dim*2, component),
                ScaleGraphic.getY(height - dim*2, component)
        );
    }

    public boolean hasPoint(double x, double y, Component component) {
        return toRectangle(component).contains(x, y);
    }

    public static Dim FULLSCREEN = new Dim(0, 0, 100, 100);


    public Dim ifXY (double x, double y) {
        return new Dim(
                x, y,
                width, height
        );
    }

    public Dim bigger(double x, double y) {
        return new Dim(
                this.x - x, this.y - y,
                width + x*2, height + y*2
        );
    }

    public Dim smaller(double x, double y) {
        return new Dim(
                this.x + x, this.y + y,
                width - x*2, height - y*2
        );
    }
    @Override
    public String toString() {
        return "Dim{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
