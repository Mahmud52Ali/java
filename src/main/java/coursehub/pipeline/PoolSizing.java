package coursehub.pipeline;

public final class PoolSizing {
	private PoolSizing() {
	}

	// Для CPU-bound не создаём больше потоков, чем ядер и независимых задач.
	public static int cpuBound(int processors, int parallelTasks) {
		if (processors < 1 || parallelTasks < 1) {
			throw new IllegalArgumentException("Число ядер и задач должно быть положительным");
		}
		return Math.min(processors, parallelTasks);
	}
}
