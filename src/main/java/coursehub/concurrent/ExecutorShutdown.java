package coursehub.concurrent;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class ExecutorShutdown {
	private ExecutorShutdown() {
	}

	// Два ограниченных ожидания; прерывание вызывающего потока не теряется.
	public static void shutdown(ExecutorService executor, Duration timeout) {
		if (timeout.isNegative() || timeout.isZero()) {
			throw new IllegalArgumentException("Таймаут должен быть положительным");
		}
		executor.shutdown();
		try {
			if (!executor.awaitTermination(timeout.toNanos(), TimeUnit.NANOSECONDS)) {
				executor.shutdownNow();
				if (!executor.awaitTermination(timeout.toNanos(), TimeUnit.NANOSECONDS)) {
					throw new IllegalStateException("Задачи не завершились после shutdownNow");
				}
			}
		} catch (InterruptedException exception) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
