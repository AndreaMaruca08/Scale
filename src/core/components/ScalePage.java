package core.components;

import core.ScaleUIApplication;
import core.utilities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Abstract class for a page</h1>
 * @since 1.0
 * @author Andrea Maruca
 */
public abstract class ScalePage extends JPanel implements Drawable {
    protected final ScaleUIApplication app;
    protected List<ScaleComponent> children;
    protected String name;
    protected boolean clicked;
    protected double xClicked = -1;
    protected double yClicked = -1;

    protected ScalePage(ScaleUIApplication app, String name) {
        this.app = app;
        this.name = name;
        children = new ArrayList<>(5);

        setLayout(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clicked = true;
                xClicked = e.getX();
                yClicked = e.getY();
                pressComponents(children);
            }
        });
    }
    protected ScalePage() {
        this(null, "Not specified");
    }

    protected void createKey(String action, Runnable runnable, String... key){
        for(String k : key){
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(k), action);
        }
        getActionMap().put(action, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
                paintImmediately(getBounds());
            }
        });

    }

    public void addScale(ScaleComponent component){
        children.add(component);
    }

    public void remove(ScaleComponent component){children.remove(component);}

    public void updateNow(Dim d){
        paintImmediately(d.toRectangle(this));
    }

    public void update(Dim d){
        repaint(d.toRectangle(this));
    }

    @Override
    public void draw(ScaleGraphic g) {}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ScaleGraphic ge = new ScaleGraphic(this, (Graphics2D) g);
        draw(ge);
        clicked = false;

        for(ScaleComponent child : children){
            child.draw(ge);
        }
    }

    private void pressComponents(List<ScaleComponent> components){
        for(ScaleComponent component : components){
            if(component instanceof ScalePressableComponent pressable){
                if(pressable.checkPress(xClicked, yClicked, this)){
                    pressable.press();
                    updateNow(pressable.getDim());
                    break;
                }
            }
        }
    }

}
