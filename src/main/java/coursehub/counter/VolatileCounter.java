package coursehub.counter;

public final class VolatileCounter implements Counter {
	private volatile int counter;

	@Override
	public void increment() {
		counter++; // Видимость есть, взаимного исключения read-modify-write нет.
	}

	@Override
	public int value() {
		return counter;
	}
}
