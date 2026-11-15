package coursehub.counter;

import coursehub.concurrent.ExecutorShutdown;
import coursehub.concurrent.NamedThreadFactory;
import java.time.Duration;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ControlledIncrementExperiment {
	// volatile намеренно: даже видимое поле не делает составную операцию атомарной.
	private volatile int counter;

	public int run(int rounds) throws Exception {
		if (rounds < 1) {
			throw new IllegalArgumentException("Нужно хотя бы одно повторение");
		}
		counter = 0;
		CyclicBarrier afterRead = new CyclicBarrier(2);
		CyclicBarrier afterWrite = new CyclicBarrier(2);
		var executor = Executors.newFixedThreadPool(2, new NamedThreadFactory("split-increment"));
		try {
			var first = executor.submit(() -> incrementInSteps(rounds, afterRead, afterWrite));
			var second = executor.submit(() -> incrementInSteps(rounds, afterRead, afterWrite));
			first.get(5, TimeUnit.SECONDS);
			second.get(5, TimeUnit.SECONDS);
			return counter;
		} finally {
			executor.shutdownNow();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
	}

	// Барьеры фиксируют допустимое для counter++ чередование двух потоков.
	private Void incrementInSteps(int rounds, CyclicBarrier afterRead, CyclicBarrier afterWrite) throws Exception {
		for (int i = 0; i < rounds; i++) {
			int observed = counter;                 // 1. Чтение
			afterRead.await(2, TimeUnit.SECONDS);    // Оба прочитали одно значение
			int next = observed + 1;                // 2. Вычисление
			counter = next;                         // 3. Запись: одна единица теряется
			afterWrite.await(2, TimeUnit.SECONDS);
		}
		return null;
	}
}
