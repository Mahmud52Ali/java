package coursehub.counter;

public final class SynchronizedMethodCounter implements Counter {
	private int counter;

	// Монитор this. Каждый завершённый increment добавляет ровно единицу.
	@Override
	public synchronized void increment() {
		counter++;
	}

	@Override
	public synchronized int value() {
		return counter;
	}
}
