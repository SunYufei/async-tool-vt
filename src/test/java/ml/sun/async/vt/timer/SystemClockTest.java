package ml.sun.async.vt.timer;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SystemClockTest {
    private final int threads = 50;
    private final int times = 1000000;

    @Test
    public void singleThread() {
        for (int t = 0; t < threads; t++) {
            long now = 0;
            LocalDateTime start = LocalDateTime.now();
            for (int i = 0; i < times; i++) {
                now = SystemClock.now();
            }
            LocalDateTime end = LocalDateTime.now();
            long duration = Duration.between(start, end).toMillis();
            System.out.println(now + " " + duration);
        }
    }

    @Test
    public void multiThread() {
        CyclicBarrier barrier = new CyclicBarrier(threads);
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        class Task implements Runnable {
            @Override
            public void run() {
                try {
                    barrier.await();
                    //
                    long now = 0;
                    LocalDateTime start = LocalDateTime.now();
                    for (int i = 0; i < times; i++) {
                        now = SystemClock.now();
                    }
                    LocalDateTime end = LocalDateTime.now();
                    long duration = Duration.between(start, end).toMillis();
                    System.out.println(now + " " + duration);
                    //
                    countDownLatch.countDown();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        try (var pool = Executors.newFixedThreadPool(threads)) {
            for (int i = 0; i < threads; i++) {
                pool.submit(new Task());
            }
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
