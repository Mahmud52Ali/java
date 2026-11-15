package coursehub.concurrent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

public final class ConcurrentBatch {
	private ConcurrentBatch() {
	}

	// Первая группа действительно готова до открытия общего старта.
	public static <T> List<T> run(int workers, int tasks, IntFunction<T> action) throws Exception {
		if (workers < 1 || tasks < 1) {
			throw new IllegalArgumentException("Нужны потоки и задачи");
		}
		var executor = Executors.newFixedThreadPool(workers, new NamedThreadFactory("batch"));
		CountDownLatch ready = new CountDownLatch(Math.min(workers, tasks));
		CountDownLatch start = new CountDownLatch(1);
		List<Future<T>> futures = new ArrayList<>();
		try {
			for (int i = 0; i < tasks; i++) {
				int index = i;
				futures.add(executor.submit(() -> {
					ready.countDown();
					start.await();
					return action.apply(index);
				}));
			}
			if (!ready.await(3, TimeUnit.SECONDS)) {
				throw new IllegalStateException("Задачи не готовы к общему старту");
			}
			start.countDown();
			List<T> results = new ArrayList<>();
			for (Future<T> future : futures) {
				results.add(future.get(5, TimeUnit.SECONDS));
			}
			return List.copyOf(results);
		} finally {
			start.countDown();
			futures.forEach(future -> future.cancel(true));
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
	}
}
