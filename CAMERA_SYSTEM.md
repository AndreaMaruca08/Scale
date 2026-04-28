# Camera System Documentation - ScaleUI v1.5

## Overview

ScaleUI ora include un **sistema di telecamera top-down completo** integrato in una classe `CameraPage` che estende `ScalePage`. Consente movimento fluido con WASD/Arrow Keys con velocità e limiti configurabili.

## Core Components

### 1. CameraController

Classe che gestisce la logica della camera indipendentemente dall'UI.

```java
CameraController camera = new CameraController(100.0);  // speed: 100 units/frame

// Movimento
camera.setVelocity(1, 0);      // Move right
camera.addVelocity(0, -1);     // Add move up
camera.update(deltaTime);      // Apply physics

// Posizione
double x = camera.getOffsetX();
double y = camera.getOffsetY();
camera.setPosition(50, 50);
camera.reset();                // Back to (0, 0)

// Limiti
camera.setBounds(-200, 200, -200, 200);
camera.setHorizontalBounds(0, 500);
camera.setVerticalBounds(0, 500);

// Velocità
camera.setSpeed(150.0);
```

### 2. CameraPage

Estensione di `ScalePage` con telecamera automatica e input WASD.

```java
public class MyGamePage extends CameraPage {
    public MyGamePage(ScaleUIApplication app) {
        super(app, "GameMap", 100.0);  // name, speed
        
        // Configura limiti
        setCameraBounds(-200, 300, -150, 250);
        
        // Aggiungi elementi
        addScale(myElement);
    }
    
    @Override
    public void draw(ScaleGraphic g) {
        // Disegna background, grid, etc.
        // Tutto è automaticamente traslato dalla camera
    }
}
```

## Usage Guide

### Basic Setup

```java
// 1. Estendi CameraPage
public class GameMap extends CameraPage {
    public GameMap(ScaleUIApplication app) {
        // Velocità default 50, puoi specificarla
        super(app, "MyMap", 75.0);
        
        // Optional: imposta limiti di movimento
        setCameraBounds(-100, 500, -100, 500);
        
        // Aggiungi componenti normalmente
        addScale(obstacle);
        addScale(player);
    }
    
    @Override
    public void draw(ScaleGraphic g) {
        // Disegna sfondo, griglie, etc.
        g.drawRect(Dim.FULLSCREEN, Color.GREEN);
    }
}

// 2. Aggiungi alla app
ScaleUIApplication app = new ScaleUIApplication("My Game");
GameMap map = new GameMap(app);
app.addPage(map);
app.changePage("MyMap");
```

### Keyboard Controls

Automatico! Non devi fare nulla, l'input è gestito internamente:

```
W / ↑  = Move up
A / ←  = Move left
S / ↓  = Move down
D / →  = Move right
```

Puoi premere più tasti contemporaneamente per movimento diagonale.

### Camera Configuration

```java
CameraPage page = new GameMap(app);

// Velocità
page.setCameraSpeed(150.0);  // Units per frame

// Limiti (non permette di andare oltre)
page.setCameraBounds(
    -200,  // minX
    200,   // maxX
    -200,  // minY
    200    // maxY
);

// Limiti singoli assi
page.setCameraHorizontalBounds(-100, 300);
page.setCameraVerticalBounds(0, 400);

// Posizione diretta
page.setCameraPosition(50, 50);

// Reset
page.resetCamera();

// Query
double x = page.getCameraX();
double y = page.getCameraY();
```

### Advanced: Accedi al Controller

```java
CameraPage page = new GameMap(app);
CameraController cam = page.getCamera();

// Controllo manuale su assi singoli
cam.setHorizontalBounds(0, 500);
cam.setVerticalBounds(0, 500);

// Clamp velocità
cam.setVelocity(0.5, 0.5);
cam.clearVelocity();

// Memoria uso
long memUsage = cam.toString().length();
```

## How It Works

### Rendering Pipeline

```
CameraPage.paintComponent() called
  ↓
Update camera position based on pressed keys
  ↓
Save Graphics2D transform
  ↓
Translate by (-cameraX, -cameraY)
  ↓
Draw all children (now offset by camera)
  ↓
Restore Graphics2D transform
```

### Key Input Handling

```
KeyListener registrato in CameraPage
  ↓
On keyPressed: Aggiungi a Set di tasti premuti
On keyReleased: Rimuovi da Set
  ↓
updateCameraVelocity() calcola velocità dal Set
  ↓
camera.update(deltaTime) applica physics
  ↓
repaint() -> renderizza con nuovo offset
```

## Practical Examples

### Example 1: Simple Map

```java
public class SimpleMap extends CameraPage {
    public SimpleMap(ScaleUIApplication app) {
        super(app, "Map", 100.0);
        setCameraBounds(-50, 150, -50, 150);
        
        // Background terrain
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                CoolBorder tile = new CoolBorder(
                    new Dim(x * 5, y * 5, 5, 5),
                    new Color(100, 150, 100),
                    0.2
                );
                addScale(tile);
            }
        }
    }
    
    @Override
    public void draw(ScaleGraphic g) {
        g.drawRect(Dim.FULLSCREEN, new Color(50, 100, 50));
    }
}
```

### Example 2: Centered Camera

```java
public class CenteredMap extends CameraPage {
    private Dim playerPos;
    
    public CenteredMap(ScaleUIApplication app) {
        super(app, "Map", 50.0);
        playerPos = new Dim(40, 40, 5, 5);
        
        // Camera segue giocatore
        setCameraPosition(40, 40);
        setCameraBounds(0, 100, 0, 100);
    }
    
    @Override
    public void draw(ScaleGraphic g) {
        // Disegna giocatore
        g.drawRect(playerPos, Color.BLUE);
        
        // Camera sposta tutto attorno al giocatore
    }
}
```

### Example 3: Boundary-Aware Camera

```java
public class LargeMapPage extends CameraPage {
    private static final int MAP_WIDTH = 500;
    private static final int MAP_HEIGHT = 500;
    private static final int VIEW_WIDTH = 100;
    private static final int VIEW_HEIGHT = 100;
    
    public LargeMapPage(ScaleUIApplication app) {
        super(app, "LargeMap", 150.0);
        
        // Limiti intelligenti
        // Camera non può mostrare fuori dalla mappa
        setCameraBounds(
            0,                           // minX: non andare a sinistra di 0
            MAP_WIDTH - VIEW_WIDTH,      // maxX: non oltre il bordo destro
            0,                           // minY: non andare sopra 0
            MAP_HEIGHT - VIEW_HEIGHT     // maxY: non oltre il bordo inferiore
        );
        
        createMap();
    }
    
    private void createMap() {
        // Crea elementi fino a 500x500
        // La camera permette di scrollare tutto, ma con limiti
    }
}
```

### Example 4: Speed Control Hotkeys

```java
public class ConfigurableMap extends CameraPage {
    public ConfigurableMap(ScaleUIApplication app) {
        super(app, "Map", 100.0);
        
        // Setup normale
        createKey("SPEED_UP", () -> {
            setCameraSpeed(getCamera().getSpeed() * 1.5);
            System.out.println("Speed: " + getCamera().getSpeed());
        }, "shift pressed");
        
        createKey("SPEED_DOWN", () -> {
            setCameraSpeed(getCamera().getSpeed() / 1.5);
        }, "shift released");
    }
}
```

## Performance Considerations

### Rendering

- **Offset**: La traslazione della camera è costosa **zero** - è solo un transform di Graphics2D
- **Culling**: Non implementato di default. Elementi fuori dallo schermo vengono comunque renderizzati
- **Optimization**: Per mappe grandi, considera disegnare solo elementi visibili

```java
// Ottimizzazione: renderizza solo ciò che vedi
@Override
public void draw(ScaleGraphic g) {
    double camX = getCameraX();
    double camY = getCameraY();
    
    for (ScaleComponent child : getChildren()) {
        Dim pos = child.getDim();
        // Skip off-screen elements
        if (pos.x() > camX + 120) continue;
        if (pos.x() < camX - 20) continue;
        // ... ecc
    }
}
```

### Input Handling

- WASD è gestito tramite KeyListener
- Supporta pressione multipla (diagonali)
- Non ha overhead durante il rendering

## API Reference

### CameraPage Methods

```java
// Setup
setCameraSpeed(double speed)
setCameraBounds(double minX, double maxX, double minY, double maxY)
setCameraHorizontalBounds(double min, double max)
setCameraVerticalBounds(double min, double max)

// Posizione
setCameraPosition(double x, double y)
resetCamera()

// Query
getCameraX()  → double
getCameraY()  → double
getCamera()   → CameraController
```

### CameraController Methods

```java
// Movimento
setVelocity(double vx, double vy)
addVelocity(double vx, double vy)
update(float deltaTime)

// Posizione
getOffsetX()  → double
getOffsetY()  → double
setPosition(double x, double y)

// Velocità
setSpeed(double speed)
getSpeed()    → double

// Limiti
setBounds(double minX, double maxX, double minY, double maxY)
setHorizontalBounds(double min, double max)
setVerticalBounds(double min, double max)

// Reset
reset()
clearVelocity()
```

## Troubleshooting

### Camera non segue il movimento

**Problema**: Premi WASD ma la camera non si muove  
**Soluzione**: Assicurati che il panel abbia focus:
```java
page.setFocusable(true);
page.requestFocusInWindow();
```

### Movimento saltato/jerky

**Problema**: Camera saltella invece di muoversi fluido  
**Soluzione**: Verifica che `lastDeltaTime` sia impostato correttamente in ScalePage

### Elementi non si muovono con camera

**Problema**: Alcuni elementi rimangono fermi  
**Soluzione**: Assicurati di aggiungerli con `addScale()`, non direttamente al panel

### Camera troppo veloce/lenta

**Problema**: Velocità del movimento non è quello che mi aspetto  
**Soluzione**: Riguarda il calcolo:
```
pixels_per_frame = speed * deltaTime
Se speed=100 e deltaTime=0.016s → 1.6px/frame
Aumenta speed per movimento più veloce
```

## Migration from BackgroundController

Se usi già `BackgroundController` per il movimento della camera, puoi migrare:

### Prima
```java
BackgroundController bgController = new BackgroundController(100);
page.setBackgroundController(bgController);
```

### Dopo
```java
public class MyPage extends CameraPage {
    public MyPage(ScaleUIApplication app) {
        super(app, "MyPage", 100.0);  // Velocità come parametro
    }
}
```

## Architecture

```
CameraPage (extends ScalePage)
├── CameraController
│   ├── offsetX, offsetY
│   ├── velocityX, velocityY
│   ├── speed
│   └── bounds (minX, maxX, minY, maxY)
├── KeyListener
│   └── Handle WASD/Arrow keys
└── paintComponent()
    └── Apply camera transform before drawing
```

## Future Enhancements

- Camera easing/smoothing
- Follow-target (seguire un oggetto)
- Shake effect (terremoti)
- Zoom support
- Isometric camera
- Split-screen multiplayer

---

**Version:** 1.5  
**Author:** Andrea Maruca  
**Date:** 2026-04-28

