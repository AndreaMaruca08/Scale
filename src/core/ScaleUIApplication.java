package core;

import core.components.ScalePage;
import core.utilities.PageManager;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>The entry point for a Scale application</h1>
 * <p>This class represents a window with a {@link PageManager} that manages pages.</p>
 *
 * @author Andrea Maruca
 * @since 1.0
 */
public final class ScaleUIApplication extends JFrame {

    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private PageManager pageManager;

    public static boolean DEBUG = false;

    public ScaleUIApplication(String title, Dimension dimension) {
        super(title);
        this.pageManager = new PageManager();

        setSize(dimension);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setContentPane(pageManager.container);
        setVisible(true);
    }

    public ScaleUIApplication(String title) {
        this(title, screenSize);
    }

    public ScaleUIApplication(String title, Dimension dimension, boolean debug) {
        this(title, dimension);
        DEBUG = debug;
    }

    public ScaleUIApplication(String title, boolean debug) {
        this(title, screenSize);
        DEBUG = debug;
    }

    public void startDebug(){
        DEBUG = true;
    }

    public void stopDebug(){
        DEBUG = false;
    }

    /**
     * Adds a new page to the application.
     * @param pagina The {@link ScalePage} instance to be added.
     */
    public void addPage(ScalePage pagina){
        pageManager.addPagina(pagina.getPageName(), pagina);
    }

    public void changePage(String pageName){
        pageManager.cambiaPagina(pageName);
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    public void resetPagine(){
        pageManager = new PageManager();
        setContentPane(pageManager.container);
    }
}
