package core.utilities;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>Cycle</h1>
 * <p>Manage a cycle of actions with a fixed duration and optional repetition, can be used as a game cycle</p>
 * @since 1.3
 * @author Andrea Maruca
 */
public class Cycle extends Timer {
    private final int[] counter = {0};
    protected int maxRepetitions;

    private long lastFrameTime = 0;
    private float deltaTime = 0.0f;

    public static final int FPS_24 = 42;
    public static final int FPS_30 = 33;
    public static final int FPS_60 = 16;
    public static final int FPS_120 = 8;
    public static final int FPS_144 = 7;
    public static final int FPS_165 = 6;
    public static final int FPS_240 = 4;
    public static final int FPS_360 = 3;
    public static final int FPS_480 = 2;

    public Cycle(int msCycleDuration, boolean repeat, int maxRepetitions) {
        this.maxRepetitions = maxRepetitions - 1;
        super(msCycleDuration, null);
        setRepeats(repeat);
    }
    public Cycle(){
        this(getMsScreenRefreshRate(), true, Integer.MAX_VALUE);
    }
    public Cycle(int cycleDuration, int maxRepetition) {
        this(cycleDuration,maxRepetition >= 1, maxRepetition);
    }
    public Cycle(int cycleDuration) {
        this(cycleDuration, true, Integer.MAX_VALUE);
    }

    public void setAction(Runnable toRun){
        stop();
        addActionListener(_ -> {
            calculateDeltaTime();

            toRun.run();
            if(!isRepeats() && maxRepetitions == 1)
                return;
            if (counter[0] >= maxRepetitions) {
                stop();
            }
            counter[0]++;
        });
    }

    private void calculateDeltaTime() {
        long currentTime = System.currentTimeMillis();

        if (lastFrameTime == 0) {
            deltaTime = 0.0f;
        } else {
            deltaTime = (currentTime - lastFrameTime) / 1000.0f;
        }

        lastFrameTime = currentTime;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public long getDeltaTimeMs() {
        return Math.round(deltaTime * 1000);
    }

    public void resetDeltaTime() {
        lastFrameTime = 0;
        deltaTime = 0.0f;
    }

    public void startCycle(){
        if(this.getActionListeners().length == 0)
            throw new IllegalStateException("No action set");
        counter[0] = 0;
        resetDeltaTime();
        stop();
        start();
    }

    public void stopCycle(){
        stop();
    }

    private static int getMsScreenRefreshRate() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        int refreshRate = gd.getDisplayMode().getRefreshRate();

        return refreshRate == -1 ? FPS_60 : 1000 / (refreshRate+5);
    }
}