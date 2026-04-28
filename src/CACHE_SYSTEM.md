# Cache System Documentation - ScaleUI v1.5

## Overview

ScaleUI now includes a **comprehensive caching system** that allows components to cache their rendering automatically. This is especially useful for components that:
- Are expensive to render (complex graphics, text wrapping, etc.)
- Change infrequently
- Are rendered many times per frame

## Core Classes

### 1. **CachedScaleComponent**
Abstract base class that provides built-in caching for normal components.

#### Key Features:
- Automatic BufferedImage caching
- Optional caching (can be disabled per-component)
- Automatic invalidation on dimension changes
- Manual cache invalidation
- Clean and transparent API

#### Usage Example:
```java
public class MyCustomComponent extends CachedScaleComponent {
    private Color bgColor;
    
    public MyCustomComponent(Dim dim) {
        super(dim);
        this.bgColor = Color.BLUE;
    }
    
    @Override
    protected void drawComponent(ScaleGraphic g) {
        // Draw your component here
        g.drawRect(dim, bgColor);
        g.drawText(dim, "Hello", Color.WHITE);
    }
    
    public void setBackground(Color color) {
        this.bgColor = color;
        invalidateCache(); // Cache is automatically rebuilt on next draw
    }
}
```

### 2. **CachedScalePressableComponent**
Extends CachedScaleComponent with support for user interaction (pressing).

```java
public class MyButton extends CachedScalePressableComponent {
    private String text;
    private Runnable action;
    
    public MyButton(Dim dim, String text) {
        super(dim);
        this.text = text;
    }
    
    @Override
    protected void drawComponent(ScaleGraphic g) {
        g.drawRoundRect(dim, 3, Color.BLUE);
        g.drawText(dim, text, Color.WHITE);
    }
    
    @Override
    public void press() {
        if (action != null) {
            action.run();
        }
    }
    
    public void setAction(Runnable action) {
        this.action = action;
    }
}
```

### 3. **Pre-built Cached Components**

ScaleUI provides cached versions of ready-to-use components:

- **CachedScaleButton** - Interactive button with caching
- **CachedScaleLabel** - Text label with caching
- **CachedCoolBorder** - Decorative border with caching

#### Usage:
```java
// Instead of ScaleButton, use CachedScaleButton
CachedScaleButton button = new CachedScaleButton(
    new Dim(40, 40, 20, 10),
    "Click Me",
    Color.BLUE,
    Color.WHITE
);
button.setAction(() -> System.out.println("Clicked!"));

// Cache can be controlled per-component
button.setCacheEnabled(false);  // Disable caching for this button
button.invalidateCache();       // Force rebuild on next draw
button.clearCache();           // Free memory immediately
```

### 4. **CachedScale2DGrid**
Enhanced grid rendering with fine-tuned cache control.

```java
CachedScale2DGrid grid = new CachedScale2DGrid(
    Dim.FULLSCREEN,
    16, 16,           // cols, rows
    6.25, 6.25,       // cell width/height in percent
    tilePainter,
    cellRenderer
);

// Control caching independently
grid.setTileCacheEnabled(true);    // Cache individual tiles
grid.setGridCacheEnabled(false);   // Don't buffer the entire grid

// Monitor memory usage
long memoryUsage = grid.getCacheMemoryUsage();
System.out.println("Grid using: " + memoryUsage + " bytes");

// Clear caches when needed
grid.clearAllCaches();
```

## CacheManager - Global Cache Management

The **CacheManager** utility class provides global control over all cached components.

### Features:
- Register/unregister components
- Invalidate all caches at once
- Enable/disable caching globally
- Cache statistics and monitoring

### Usage:
```java
// Register components
CacheManager.register(myButton);
CacheManager.register(myLabel);

// Global operations
CacheManager.invalidateAll();           // Rebuild all caches
CacheManager.clearAll();                // Free all cache memory
CacheManager.setGlobalCacheEnabled(false); // Disable caching globally

// Statistics
CacheManager.enableStats();
// ... do stuff ...
CacheManager.printStats();
/*
=== Cache Statistics ===
Registered Components: 5
Cache Hits: 142
Cache Misses: 8
Hit Ratio: 94.67%
*/
```

## Performance Guidelines

### When to Use Caching

✅ **USE Caching For:**
- Buttons and labels (static appearance)
- Complex borders (expensive to draw)
- Decorative elements (rarely change)
- Text-heavy components (text wrapping is expensive)
- Components drawn 60+ times per second

❌ **AVOID Caching For:**
- Animated components (cache becomes invalid frequently)
- Components with per-frame state changes
- Simple shapes (drawRect, drawLine, etc.)
- Very large components (memory concerns)

### Memory Considerations

Each cached component uses memory proportional to its pixel size:
- **Small button (100×50 px)**: ~20KB
- **Medium label (300×200 px)**: ~240KB
- **Large grid (1000×600 px)**: ~2.4MB

Use `getCacheMemoryUsage()` to monitor memory usage.

## Best Practices

### 1. Invalidate Cache When Data Changes
```java
public class DataLabel extends CachedScaleComponent {
    private String data;
    
    public void setData(String newData) {
        this.data = newData;
        invalidateCache(); // Important!
    }
    
    @Override
    protected void drawComponent(ScaleGraphic g) {
        g.drawText(dim, data, Color.BLACK);
    }
}
```

### 2. Use the Right Base Class
```java
// For interactive components
public class MyButton extends CachedScalePressableComponent { }

// For simple display components
public class MyLabel extends CachedScaleComponent { }

// For non-cached custom components
public class MyAnimated extends ScaleComponent { }
```

### 3. Register for Global Management (Optional)
```java
CachedScaleButton button = new CachedScaleButton(...);
CacheManager.register(button);  // Now it's managed globally
```

### 4. Disable Caching for Animated Components
```java
// This component changes every frame
public class AnimatedSprite extends CachedScaleComponent {
    public AnimatedSprite(Dim dim) {
        super(dim);
        setCacheEnabled(false);  // Disable caching for animations
    }
    
    @Override
    protected void drawComponent(ScaleGraphic g) {
        // Draw animation frame
    }
}
```

## Migration from Old Components

Migrating existing components to use caching is simple:

### Old Code (No Cache)
```java
public class MyButton extends ScaleButton {
    // ... implementation ...
}
```

### New Code (With Cache)
```java
public class MyButton extends CachedScaleButton {
    // ... same implementation ...
    // Caching is automatic!
}
```

## Architecture

The caching system is designed to be:

1. **Non-intrusive** - Components work with or without caching
2. **Optional** - Users choose what to cache and what not to
3. **Transparent** - Api is the same, caching happens underneath
4. **Scalable** - From single components to global management
5. **Efficient** - Cache rebuilds only when necessary

## Troubleshooting

### Cache Not Updating
**Problem:** Component shows old rendering after state change
**Solution:** Call `invalidateCache()` when component state changes

```java
button.setText("New Text");
button.invalidateCache();  // Force rebuild
```

### Memory Growing
**Problem:** Using too much RAM with many cached components
**Solution:** Use CacheManager to monitor and clear unused caches

```java
CacheManager.printStats();
CacheManager.clearAll();  // Free all memory
// Rebuild caches will happen on next render
```

### Performance Not Improving
**Problem:** Caching doesn't improve performance
**Solution:** Profile your components - only cache expensive renders

```java
// If drawing to cache takes long, the component is not expensive enough
// Or if cache is rebuilt every frame, caching won't help
button.setCacheEnabled(false);  // Disable if not helping
```

## Future Enhancements

Possible future improvements:
- Lossy cache eviction (LRU cache)
- Memory pooling for BufferedImages
- Cache compression for large components
- Async cache building
- Cache profiling tools

---

**Author:** Andrea Maruca  
**Version:** 1.5  
**License:** Same as ScaleUI

