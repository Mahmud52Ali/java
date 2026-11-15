package coursehub.worker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class PlainStopProbe {
	private boolean stopRequested;
	private long iterations;

	// Некорректный worker изолирован в дочерней JVM, чтобы не оставить зависший поток.
	public static void main(String[] args) throws InterruptedException {
		PlainStopProbe probe = new PlainStopProbe();
		CountDownLatch started = new CountDownLatch(1);
		Thread worker = new Thread(() -> {
			started.countDown();
			while (!probe.stopRequested) {
				probe.iterations++;
			}
		}, "plain-stop-worker");
		worker.start();
		if (!started.await(1, TimeUnit.SECONDS)) {
			throw new IllegalStateException("Worker не запустился");
		}
		// Даём JIT нагрузку без sleep; это исследование, а не синхронизация флага.
		long checksum = 0;
		for (long i = 0; i < 20_000_000; i++) {
			checksum += i;
		}
		probe.stopRequested = true;
		worker.join(200);
		System.out.println("Обычный флаг: завершился=" + !worker.isAlive() + ", checksum=" + checksum);
		System.out.flush();
		System.exit(0);
	}
}
