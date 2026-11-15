package coursehub.counter;

public final class SynchronizedBlockCounter implements Counter {
	private final Object monitor = new Object();
	private int counter;

	@Override
	public void increment() {
		// Тот же инвариант; монитор закрыт от внешнего кода.
		synchronized (monitor) {
			counter++;
		}
	}

	@Override
	public int value() {
		synchronized (monitor) {
			return counter;
		}
	}
}
