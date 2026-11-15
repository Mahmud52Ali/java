package coursehub.worker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class VolatileStopWorker implements Runnable {
	private volatile boolean stopRequested;
	private final CountDownLatch started = new CountDownLatch(1);
	private long iterations;

	@Override
	public void run() {
		started.countDown();
		while (!stopRequested) {
			iterations++;
		}
	}

	public void requestStop() {
		stopRequested = true;
	}

	public boolean awaitStarted() throws InterruptedException {
		return started.await(2, TimeUnit.SECONDS);
	}

	// Читать после Future.get или join: они публикуют итог iterations.
	public long iterations() {
		return iterations;
	}
}
