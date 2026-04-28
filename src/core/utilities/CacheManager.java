package core.utilities;

import core.components.CachedScaleComponent;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>CacheManager</h1>
 * <p>
 * Utility class for managing cached components globally.
 * Provides methods to invalidate, clear, and monitor caches across the application.
 * </p>
 * <p>
 * This is useful for batch operations like theme changes or screen resizes where
 * multiple components need cache invalidation.
 * </p>
 *
 * @since 1.5
 * @author Andrea Maruca
 */
public class CacheManager {
    private static final List<CachedScaleComponent> registeredComponents = new ArrayList<>();
    private static int cacheHits = 0;
    private static int cacheMisses = 0;
    private static boolean statsEnabled = false;

    /**
     * Registers a cached component for management.
     *
     * @param component the component to register
     */
    public static void register(CachedScaleComponent component) {
        if (!registeredComponents.contains(component)) {
            registeredComponents.add(component);
        }
    }

    /**
     * Unregisters a cached component.
     *
     * @param component the component to unregister
     */
    public static void unregister(CachedScaleComponent component) {
        registeredComponents.remove(component);
    }

    /**
     * Invalidates the cache for all registered components.
     */
    public static void invalidateAll() {
        for (CachedScaleComponent component : registeredComponents) {
            component.invalidateCache();
        }
    }

    /**
     * Clears the cache for all registered components.
     */
    public static void clearAll() {
        for (CachedScaleComponent component : registeredComponents) {
            component.clearCache();
        }
    }

    /**
     * Enables or disables caching for all registered components.
     *
     * @param enabled true to enable caching, false to disable
     */
    public static void setGlobalCacheEnabled(boolean enabled) {
        for (CachedScaleComponent component : registeredComponents) {
            component.setCacheEnabled(enabled);
        }
    }

    /**
     * Gets the number of registered cached components.
     *
     * @return the number of registered components
     */
    public static int getRegisteredComponentCount() {
        return registeredComponents.size();
    }

    /**
     * Enables cache statistics tracking.
     */
    public static void enableStats() {
        statsEnabled = true;
        cacheHits = 0;
        cacheMisses = 0;
    }

    /**
     * Disables cache statistics tracking.
     */
    public static void disableStats() {
        statsEnabled = false;
    }

    /**
     * Records a cache hit (internal use).
     *
     * @return true if stats are enabled
     */
    static boolean recordHit() {
        if (statsEnabled) {
            cacheHits++;
            return true;
        }
        return false;
    }

    /**
     * Records a cache miss (internal use).
     *
     * @return true if stats are enabled
     */
    static boolean recordMiss() {
        if (statsEnabled) {
            cacheMisses++;
            return true;
        }
        return false;
    }

    /**
     * Gets cache hit count.
     *
     * @return number of cache hits
     */
    public static int getCacheHits() {
        return cacheHits;
    }

    /**
     * Gets cache miss count.
     *
     * @return number of cache misses
     */
    public static int getCacheMisses() {
        return cacheMisses;
    }

    /**
     * Gets the cache hit ratio.
     *
     * @return hit ratio as percentage (0.0 - 100.0), or 0 if no accesses
     */
    public static double getCacheHitRatio() {
        int total = cacheHits + cacheMisses;
        if (total == 0) return 0.0;
        return (cacheHits * 100.0) / total;
    }

    /**
     * Resets statistics.
     */
    public static void resetStats() {
        cacheHits = 0;
        cacheMisses = 0;
    }

    /**
     * Prints cache statistics to stdout.
     */
    public static void printStats() {
        System.out.println("=== Cache Statistics ===");
        System.out.println("Registered Components: " + getRegisteredComponentCount());
        System.out.println("Cache Hits: " + getCacheHits());
        System.out.println("Cache Misses: " + getCacheMisses());
        System.out.printf("Hit Ratio: %.2f%%\n", getCacheHitRatio());
    }
}

