# ScaleUI Cache System - Quick Start Guide

## 5-Minute Setup

### 1. Replace Your Components

Instead of:
```java
ScaleButton button = new ScaleButton(dim, "Click", Color.BLUE, Color.WHITE);
```

Use:
```java
CachedScaleButton button = new CachedScaleButton(dim, "Click", Color.BLUE, Color.WHITE);
```

The API is **exactly the same**. Caching is automatic.

### 2. Handle Dynamic Changes

When properties change, let the cache know:

```java
CachedScaleLabel label = new CachedScaleLabel(dim, "Initial");

button.setAction(() -> {
    label.setText("Updated");  // This auto-invalidates cache
    page.update(label.getDim());
});
```

Most setter methods call `invalidateCache()` automatically.

### 3. Global Management (Optional)

For fine control over all cached components:

```java
// Register components you want to manage globally
CacheManager.register(button1);
CacheManager.register(label1);

// Later...
CacheManager.invalidateAll();              // Rebuild all
CacheManager.clearAll();                   // Free memory
CacheManager.setGlobalCacheEnabled(false); // Disable all

// Check stats
CacheManager.printStats();
```

## Component Choices

### For Static UI Elements
✅ **Use Cached:** Buttons, labels, borders, unchanging text

```java
CachedScaleButton button = new CachedScaleButton(...);
CachedScaleLabel label = new CachedScaleLabel(...);
CachedCoolBorder border = new CachedCoolBorder(...);
```

### For Dynamic/Animated Content
❌ **Don't Use Cached:** Animated sprites, per-frame changes, or optional disable:

```java
// Option 1: Don't use cached component
public class Animation extends ScaleComponent { }

// Option 2: Use cached but disable
CachedScaleComponent comp = new MyComp(dim);
comp.setCacheEnabled(false);  // Renders every frame without cache
```

### Undecided?
Start with **non-cached**, measure performance, switch to cached if needed.

## Code Templates

### Template 1: Simple Cached Component
```java
public class MyLabel extends CachedScaleLabel {
    public MyLabel(Dim dim, String text) {
        super(dim, text);
    }
    // Everything inherited, including caching
}
```

### Template 2: Cached Interactive Component
```java
public class MyButton extends CachedScaleButton {
    public MyButton(Dim dim) {
        super(dim, "", true, Color.BLUE, Color.WHITE);
        setAction(this::onPressed);
    }
    
    private void onPressed() {
        System.out.println("Pressed!");
    }
}
```

### Template 3: Custom Cached Component
```java
public class MyCustom extends CachedScaleComponent {
    private String text;
    
    public MyCustom(Dim dim, String text) {
        super(dim);
        this.text = text;
    }
    
    @Override
    protected void drawComponent(ScaleGraphic g) {
        // Your drawing code here
        g.drawRect(dim, Color.BLUE);
        g.drawText(dim, text, Color.WHITE);
    }
    
    public void setText(String newText) {
        this.text = newText;
        invalidateCache();  // Always do this!
    }
}
```

## Common Tasks

### How to... disable cache for a component?
```java
button.setCacheEnabled(false);
```

### How to... force cache rebuild?
```java
button.invalidateCache();
```

### How to... free cache memory?
```java
button.clearCache();
// Or globally:
CacheManager.clearAll();
```

### How to... check memory usage?
```java
long bytes = grid.getCacheMemoryUsage();
System.out.println("Using: " + bytes / 1024 + "KB");
```

### How to... track cache performance?
```java
CacheManager.enableStats();

// ... use your UI ...

CacheManager.printStats();
// Output:
// Registered Components: 5
// Cache Hits: 142
// Cache Misses: 8
// Hit Ratio: 94.67%
```

### How to... switch all caching off?
```java
CacheManager.setGlobalCacheEnabled(false);

// Or per-component:
button.setCacheEnabled(false);
label.setCacheEnabled(false);
```

## Troubleshooting

### Cache doesn't update after setText()
**Problem:** Component shows old text  
**Solution:** Most setters auto-call invalidateCache(). Verify it's being called:
```java
label.setText("New"); // Automatically invalidates
// If custom property:
myProperty = value;
invalidateCache();  // Call manually
```

### RAM growing indefinitely
**Problem:** Too many cached components  
**Solution:** Monitor and clear when needed
```java
if (CacheManager.getRegisteredComponentCount() > 100) {
    CacheManager.clearAll();  // Free memory
}
```

### Performance not improving
**Problem:** Caching doesn't help  
**Solution:** Disable it - not everything benefits
```java
// If component simple enough that direct rendering
// is faster than cache management:
button.setCacheEnabled(false);
```

### Cache not working in custom component
**Problem:** Drawing doesn't cache  
**Solution:** Override drawComponent(), not draw()
```java
// WRONG:
@Override
public void draw(ScaleGraphic g) { }  // Override draw()

// RIGHT:
@Override
protected void drawComponent(ScaleGraphic g) { }  // Override drawComponent()
```

## Performance Expectations

### Typical Improvements
- **Static buttons**: 30-50% faster
- **Text labels**: 40-60% faster (text wrapping is expensive)
- **Complex graphics**: 20-40% faster
- **Simple shapes**: 0-10% improvement (not worth it)
- **Animations**: 0% (cache rebuilds every frame)

### Memory Costs
```
Tiny button (50×50):     ~10KB
Small button (100×100):  ~40KB
Medium label (300×200):  ~240KB
Large grid (1000×600):   ~2.4MB
```

Use CacheManager to monitor and optimize.

## Migration Checklist

- [ ] Identify which components benefit from caching
- [ ] Replace with Cached* versions
- [ ] Test no rendering regressions
- [ ] Run performance profiler
- [ ] Use CacheManager for global control
- [ ] Monitor with stats if needed
- [ ] Adjust based on profiling

## Next Reading

1. **CACHE_SYSTEM.md** - Full detailed guide
2. **ARCHITECTURE.md** - Design deep dive
3. **TestCachedComponents.java** - Working example

## Support

Questions about specific scenarios? Check:
- CACHE_SYSTEM.md - Best practices section
- ARCHITECTURE.md - When to use section  
- TestCachedComponents.java - Example implementations

---

**Still not sure if caching helps?**  
Try it! Switch one component to cached version and measure with:
```java
CacheManager.enableStats();
button.setCacheEnabled(true);  // Do stuff
CacheManager.printStats();
```

**Version:** 1.5  
**Date:** 2026-04-28  

