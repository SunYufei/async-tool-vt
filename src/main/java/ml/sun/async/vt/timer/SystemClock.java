package ml.sun.async.vt.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class SystemClock {
    public static long now() {
        return InstanceHolder.INSTANCE.now.get();
    }

    private static class InstanceHolder {
        private static final SystemClock INSTANCE = new SystemClock();
    }

    private final AtomicLong now;

    private SystemClock() {
        this.now = new AtomicLong(System.currentTimeMillis());
        Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "SystemClock");
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), 1L, 1L, TimeUnit.MILLISECONDS);
    }
}
