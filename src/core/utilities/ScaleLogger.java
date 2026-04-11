package core.utilities;

import core.components.ScalePage;

/**
 * <h1>Default logger for Scale applications</h1>
 * @since 1.2
 * @author Andrea Maruca
 */
public final class ScaleLogger {
    public static void log(String message, ScalePage... parents) {
        IO.println(PREFIX(parents) + message);
    }

    private static String PREFIX (ScalePage... parents){
        StringBuilder sb = new StringBuilder();
        for (ScalePage parent : parents) {
            sb.append(parent.getPageName() == null ? "undefined_name" : parent.getPageName()).append(" ->   ");
        }
        return "[ScaleLogger] " + sb;
    }
}
