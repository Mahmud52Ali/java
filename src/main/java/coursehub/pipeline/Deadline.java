package coursehub.pipeline;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

final class Deadline {
	private final long started = System.nanoTime();
	private final long timeoutNanos;

	Deadline(Duration timeout) {
		if (timeout.isNegative() || timeout.isZero()) {
			throw new IllegalArgumentException("Таймаут должен быть положительным");
		}
		timeoutNanos = timeout.toNanos();
	}

	long remainingNanos() throws TimeoutException {
		long remaining = timeoutNanos - (System.nanoTime() - started);
		if (remaining <= 0) {
			throw new TimeoutException("Истекло время обработки заявок");
		}
		return remaining;
	}
}
