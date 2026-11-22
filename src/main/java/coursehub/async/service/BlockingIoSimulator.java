package coursehub.async.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class BlockingIoSimulator {
    private final ScheduledExecutorService responseExecutor;

    public BlockingIoSimulator(ScheduledExecutorService responseExecutor) {
        this.responseExecutor = responseExecutor;
    }

    public String read(String value, long responseDelayMillis) throws InterruptedException {
        CountDownLatch response = new CountDownLatch(1);
        ScheduledFuture<?> responseSignal = responseExecutor.schedule(
                response::countDown, responseDelayMillis, TimeUnit.MILLISECONDS);
        try {
            response.await();
            return value;
        } finally {
            responseSignal.cancel(false);
        }
    }
}
