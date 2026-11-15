package coursehub.demo;

import coursehub.counter.ControlledIncrementExperiment;
import coursehub.counter.CounterExperiment;
import coursehub.counter.SynchronizedBlockCounter;
import coursehub.counter.SynchronizedMethodCounter;
import coursehub.counter.UnsafeCounter;
import coursehub.counter.VolatileCounter;

public final class Task01LostIncrements {
	private Task01LostIncrements() {
	}

	public static void run() throws Exception {
		System.out.println("\nЗадание 1. Потерянные инкременты");
		int expected = 4 * 100_000;
		for (int attempt = 1; attempt <= 5; attempt++) {
			System.out.println("counter++: запуск=" + attempt + ", ожидалось=" + expected
					+ ", получено=" + CounterExperiment.run(new UnsafeCounter(), 4, 100_000));
		}
		System.out.println("Разложенный volatile-инкремент: ожидалось=200, получено="
				+ new ControlledIncrementExperiment().run(100));
		System.out.println("volatile counter++: " + CounterExperiment.run(new VolatileCounter(), 4, 100_000));
		System.out.println("synchronized method: " + CounterExperiment.run(new SynchronizedMethodCounter(), 4, 100_000));
		System.out.println("synchronized block: " + CounterExperiment.run(new SynchronizedBlockCounter(), 4, 100_000));
	}
}
