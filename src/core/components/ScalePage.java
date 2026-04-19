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
 * <p>Abstract class for a page, provides basic functionalities for a page such as managing children components and handling user input.
 * <br>Can also function as a game manager with a scrolling function of the screen </p>
 * @since 1.0
 * @author Andrea Maruca
 */
public abstract class ScalePage extends JPanel implements Drawable {
    protected final ScaleUIApplication app;
    protected List<ScaleComponent> children;
    protected String pageName;
    protected boolean clicked;
    protected double xClicked = -1;
    protected double yClicked = -1;

    protected BackgroundController bgController;
    protected Cycle gameCycle;
    protected float lastDeltaTime = 0f;

    protected ScalePage(ScaleUIApplication app, String name) {
        this.app = app;
        this.pageName = name;
        children = new ArrayList<>(5);
        bgController = null;

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

    public String getPageName(){
        return pageName;
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
        Graphics2D g2 = (Graphics2D) g;
        ScaleGraphic ge = new ScaleGraphic(this, g2);

        draw(ge);
        clicked = false;

        if(ScaleUIApplication.DEBUG)
            ScaleLogger.log("DRAW CYCLE STARTED FOR " + pageName + "\n", this);

        if (bgController == null) {
            for(ScaleComponent child : children){
                debug(child);
                child.draw(ge);
            }
            return;
        }

        bgController.update(lastDeltaTime, ge);
        int offsetPx = (int) bgController.getOffsetX();
        g2.translate(offsetPx, 0);

        for(ScaleComponent child : children){
            debug(child);
            child.draw(ge);
        }

        g2.translate(-offsetPx, 0);
    }
    private void pressComponents(List<ScaleComponent> components){
        for(ScaleComponent component : components){
            if(component instanceof ScalePressableComponent pressable){
                if(pressable.checkPress(xClicked, yClicked, this)){
                    pressable.press();
                    update(pressable.getDim());
                    if(ScaleUIApplication.DEBUG)
                        ScaleLogger.log("Pressed: " + pressable.name, this);
                    break;
                }
            }
        }
    }

    private void debug(ScaleComponent component){
        if(!ScaleUIApplication.DEBUG)
            return;

        ScaleLogger.log("Drawn: " + component.name + " at " + component.getDim(), this);
    }

    public float getBackgroundOffsetX() {
        return bgController != null ? bgController.getOffsetX() : 0;
    }

    protected void setBackgroundController(BackgroundController controller) {
        this.bgController = controller;
    }

    protected void startGameCycle(int msPerFrame) {
        if (gameCycle != null) gameCycle.stopCycle();

        gameCycle = new Cycle(msPerFrame);
        gameCycle.setAction(() -> {
            lastDeltaTime = gameCycle.getDeltaTime();
            repaint();
        });
        gameCycle.startCycle();
    }

    protected void clickListener(Runnable runnable) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                runnable.run();
                paintImmediately(getBounds());
            }
        });
    }

    protected void stopGameCycle() {
        if (gameCycle != null) gameCycle.stopCycle();
    }
}