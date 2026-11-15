package coursehub.counter;

import coursehub.concurrent.ExecutorShutdown;
import coursehub.concurrent.NamedThreadFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class CounterExperiment {
	private CounterExperiment() {
	}

	public static int run(Counter counter, int workers, int increments) throws Exception {
		if (workers < 1 || increments < 0) {
			throw new IllegalArgumentException("Некорректная нагрузка");
		}
		var executor = Executors.newFixedThreadPool(workers, new NamedThreadFactory("counter"));
		CountDownLatch ready = new CountDownLatch(workers);
		CountDownLatch start = new CountDownLatch(1);
		List<Future<?>> results = new ArrayList<>();
		try {
			for (int i = 0; i < workers; i++) {
				results.add(executor.submit(() -> {
					ready.countDown();
					start.await();
					for (int j = 0; j < increments; j++) {
						counter.increment();
					}
					return null;
				}));
			}
			if (!ready.await(5, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Потоки счётчика не готовы");
			}
			start.countDown();
			for (Future<?> result : results) {
				result.get(5, TimeUnit.SECONDS);
			}
			return counter.value();
		} finally {
			start.countDown();
			results.forEach(result -> result.cancel(true));
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
	}
}
