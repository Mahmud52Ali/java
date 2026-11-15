package coursehub.counter;

public final class UnsafeCounter implements Counter {
	private int counter;

	@Override
	public void increment() {
		counter++;
	}

	@Override
	public int value() {
		return counter;
	}
}
