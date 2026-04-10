package core.utilities;

import core.ScalePage;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * <h1>PageManager</h1>
 * <p>Manages {@link ScalePage} as pages in a card layout.</p>
 * @author Andrea Maruca
 * @since 1.0
 */
public class PageManager extends CardLayout {
    public JPanel container;


    public PageManager() {
        super();
        container = new JPanel(this);
    }

    /**
     * Cambia la pagina attiva
     * @param nomePagina nome della pagina
     */
    public void cambiaPagina(String nomePagina) {
        show(container, nomePagina);
    }

    /**
     * Aggiunge una pagina alla lista, se già presente non lo aggiunge di nuovo
     * @param nomePagina nome della pagina
     * @param pagina pagina da aggiungere
     */
    public void addPagina(String nomePagina, ScalePage pagina) {
        if(Arrays.stream(container.getComponents()).toList().contains(pagina))
            return;
        container.add(pagina, nomePagina);
    }

}
