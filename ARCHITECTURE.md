# ScaleUI Architecture Documentation - v1.5

## Overview

ScaleUI è una libreria moderna per Swing che implementa un **sistema di coordinate normalizzate (0-100)** indipendente dalla risoluzione dello schermo, combinato con un **sistema di caching intelligente** per ottimizzare le prestazioni.

## Architettura di Base

### 1. Sistema di Coordinate Normalizzate

**Dim** - Record che rappresenta dimensioni in coordinate 0-100
```
Dim(x=10, y=10, width=20, height=20)
significa: posiziona il componente al 10% da sinistra e sinistra,
con larghezza del 20% e altezza del 20% della pagina
```

**ScaleGraphic** - Context grafico che trasforma le coordinate
```
getX(50) -> 50% della larghezza della pagina in pixel
getY(50) -> 50% dell'altezza della pagina in pixel
```

### 2. Gerarchia dei Componenti

```
Drawable (interface)
  ↓
ScaleComponent (abstract base)
  ├─ ScalePage (container per componenti)
  ├─ CachedScaleComponent (base con caching)
  │  └─ CachedScalePressableComponent
  └─ ScalePressableComponent (input handling)
     └─ CachedScalePressableComponent
```

## System di Caching - Nuovo

### Design Philosophy
- **Optional**: Non forzato, ogni componente sceglie se usare il cache
- **Transparent**: API identica, caching sottostante
- **Efficient**: Rebuild solo quando necessario
- **Clean**: Architettura pulita senza workarounds

### Componenti Cached

#### CachedScaleComponent
Base per componenti che vogliono cache rendering.

```java
public abstract class CachedScaleComponent extends ScaleComponent {
    protected abstract void drawComponent(ScaleGraphic g);
    public void invalidateCache()  // Invalida cache
    public void setCacheEnabled(boolean) // Enable/disable
}
```

**Workflow:**
1. draw() viene chiamato
2. Se cache valido → disegna BufferedImage
3. Se cache invalido/nuovo → rebuild via drawComponent()
4. Salva in cache

#### CachedScalePressableComponent
Combina caching + input handling

```java
public abstract class CachedScalePressableComponent extends CachedScaleComponent implements Pressable {
    @Override
    public boolean checkPress(double x, double y, Component parent)
    public void press() // Subclasses implement
}
```

### Componenti Ready-made Cached

1. **CachedScaleButton** - Bottone interattivo con cache
2. **CachedScaleLabel** - Etichetta con cache (utile per text wrapping)
3. **CachedCoolBorder** - Bordo decorativo con cache

```java
CachedScaleButton btn = new CachedScaleButton(dim, "Click", Color.BLUE, Color.WHITE);
btn.setAction(() -> System.out.println("Clicked!"));

// Control cache per questo componente
btn.invalidateCache();      // Rebuild
btn.setCacheEnabled(false); // Disabilita cache
```

### CachedScale2DGrid - Grid 2D Avanzato

Estensione di Scale2DGrid con finer-grained cache control:

```java
CachedScale2DGrid grid = new CachedScale2DGrid(...);

// Control tile e grid buffer indipendentemente
grid.setTileCacheEnabled(true);     // Cache singoli tiles
grid.setGridCacheEnabled(false);    // Disabilita grid buffer

// Monitoring
long memUsage = grid.getCacheMemoryUsage(); // In bytes
grid.clearAllCaches(); // Free memory
```

### CacheManager - Global Control

Utility per gestire tutti i cached components:

```java
// Registrazione
CacheManager.register(button);
CacheManager.register(label);

// Operazioni globali
CacheManager.invalidateAll();           // Rebuild tutti
CacheManager.clearAll();                // Free memoria
CacheManager.setGlobalCacheEnabled(false);

// Statistiche
CacheManager.enableStats();
// ... do stuff ...
CacheManager.printStats();
// Output:
// Registered Components: 5
// Cache Hits: 142
// Cache Misses: 8
// Hit Ratio: 94.67%
```

## Implementazione Interna

### MockPage - Internal Trick

Per renderizzare un componente nel cache senza una vera pagina:

```java
public static class MockPage extends ScalePage {
    private final int width, height;
    // Overrides getWidth/getHeight con valori fissi
    // Permette a ScaleGraphic di funzionare correttamente
}
```

Durante il cache rendering:
1. Crea MockPage con dimensioni in pixel del componente
2. Passa a drawComponent() via ScaleGraphic temporario
3. Disegna su BufferedImage

### Invalidation Strategy

Cache invalido quando:
- Dimensioni cambiano → ricompila automaticamente  
- Metodo invalidateCache() chiamato → manual invalidation
- Property sensibile cambia (setText, setColor, etc.)

## Guida all'Uso

### Scenario 1: Button Statico
```java
// Before (no cache)
public class MyButton extends ScalePressableComponent { }

// After (with cache)
public class MyButton extends CachedScalePressableComponent { }
```
No altre modifiche! Caching automatico.

### Scenario 2: Label che Cambia
```java
CachedScaleLabel label = new CachedScaleLabel(dim, "Initial");
CacheManager.register(label);

button.setAction(() -> {
    label.setText("Updated");
    // invalidateCache() è chiamato automaticamente in setText()
});
```

### Scenario 3: Disabilitare Cache per Animazioni
```java
public class AnimatedSprite extends CachedScaleComponent {
    public AnimatedSprite(Dim dim) {
        super(dim);
        setCacheEnabled(false); // No caching for animations
    }
    
    @Override
    protected void drawComponent(ScaleGraphic g) {
        // Draw current animation frame
    }
}
```

### Scenario 4: Grid con Cache Controllato
```java
CachedScale2DGrid grid = new CachedScale2DGrid(...);

// Save memory on mobile
grid.setTileCacheEnabled(false);
grid.setGridCacheEnabled(false);

// Ricompila quando dispositivo ha RAM
grid.setTileCacheEnabled(true);
```

## Performance Tips

### Quando il Cache Aiuta
✅ Componenti **statici** - Bottoni, label, border  
✅ **Text-heavy** - Text wrapping è costoso  
✅ **Complex graphics** - Molti disegni  
✅ Renderizzati **60+ volte/sec** - Riusare risultato  

### Quando NON Aiuta
❌ **Animazioni** - Cache invalido ogni frame  
❌ **Cambiano ogni frame** - Per-frame state  
❌ **Forme semplici** - drawRect è già veloce  
❌ **Componenti enormi** - Costo memoria > beneficio  

## Memory Management

### Stima Memoria
- Bottone 100×50: ~20KB  
- Label 300×200: ~240KB  
- Grid 1000×600: ~2.4MB  

```java
long bytes = grid.getCacheMemoryUsage();
System.out.println("Using: " + bytes / 1024 + "KB");

// Pulisci quando necessario
CacheManager.clearAll();
```

## Compatibilità

### Backward Compatibility
- Esiste ancora **Scale2DGrid** (no cache)
- Esistono ancora **ScaleButton**, **ScaleLabel**, etc. (no cache)
- **Cached versions sono aggiuntive**, non replacement
- Codice vecchio funziona without change

### Migration Path
1. Mantieni componenti non-cached se funzionano
2. Introduce versioni cached dove serve performance
3. Usa CacheManager per fine-tuning globale
4. Profile per validare i benefici

## File Structure

```
src/core/
├── components/
│   ├── Drawable.java
│   ├── ScaleComponent.java              (Base)
│   ├── CachedScaleComponent.java        (NEW)
│   ├── CachedScalePressableComponent.java (NEW)
│   ├── ScalePressableComponent.java
│   ├── ScalePage.java
│   └── ...
├── game2d/
│   ├── Scale2DGrid.java                (Original)
│   ├── CachedScale2DGrid.java           (NEW - Enhanced)
│   ├── CellRenderer.java
│   └── TilePainter.java
├── readycomponents/
│   ├── ScaleButton.java                (Original)
│   ├── CachedScaleButton.java           (NEW)
│   ├── ScaleLabel.java                 (Original)
│   ├── CachedScaleLabel.java            (NEW)
│   ├── CoolBorder.java                 (Original)
│   ├── CachedCoolBorder.java            (NEW)
│   └── ...
└── utilities/
    ├── ScaleGraphic.java               (Core graphics)
    ├── Dim.java                        (Coordinates)
    ├── CacheManager.java               (NEW - Global cache management)
    └── ...
```

## Prossimi Passi (Future Work)

- LRU Cache eviction per memory-constrained environments
- Async cache building per non-blocking rendering
- Cache statistics e profiling built-in
- Memory pooling per BufferedImage reuse
- Cache compression per large components

---

**Versione:** 1.5  
**Author:** Andrea Maruca  
**Data:** 2026-04-28

