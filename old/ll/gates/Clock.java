package ll.gates;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by gvoiron on 17/10/17.
 * Time : 19:05
 */
public final class Clock extends AGate {

    private final double freq;
    private final double high;
    private final boolean start;
    private boolean currentOutput;
    private ScheduledFuture<?> clock;
    private ScheduledExecutorService scheduler;
    private ScheduledExecutorService highScheduler;
    private ScheduledFuture<?> highClock;

    public Clock(String name, boolean start, double freq) {
        this(name, start, freq, 1.0 / freq / 2);
    }

    public Clock(String name, boolean start, double freq, double high) {
        super(name, start);
        this.freq = freq;
        this.start = start;
        this.currentOutput = start;
        this.high = high;
        this.clock = null;
        this.highClock = null;
    }

    @Override
    boolean simulate(Map<AGate, Boolean> gatesValues) {
        simulateHigh();
        if (clock == null) {
            scheduler = Executors.newScheduledThreadPool(1);
            long period = Math.round(1.0E9 / freq);
            clock = scheduler.scheduleAtFixedRate(this::simulateHigh, period, period, TimeUnit.NANOSECONDS);
        }
        return currentOutput;
    }

    private void simulateHigh() {
        currentOutput = start;
        if (highScheduler == null) {
            highScheduler = Executors.newScheduledThreadPool(1);
        }
        long highTime = Math.round(high * 1.0E9);
        long period = Math.round(1.0E9 / freq);
        if (currentOutput) {
            highClock = highScheduler.schedule(() -> currentOutput = false, highTime, TimeUnit.NANOSECONDS);
        } else {
            highClock = highScheduler.schedule(() -> currentOutput = true, period - highTime, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public void reset() {
        if (clock != null) {
            clock.cancel(true);
            scheduler.shutdown();
        }
        if (highScheduler != null) {
            highClock.cancel(true);
            highScheduler.shutdown();
        }
    }

}
