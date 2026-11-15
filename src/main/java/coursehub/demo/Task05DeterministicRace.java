package coursehub.demo;

import coursehub.counter.ControlledIncrementExperiment;

public final class Task05DeterministicRace {
	private Task05DeterministicRace() {
	}

	public static void run() throws Exception {
		System.out.println("\nЗадание 5. Детерминированный конкурентный тест");
		System.out.println("CyclicBarrier: два чтения до двух записей; ожидалось=200, получено="
				+ new ControlledIncrementExperiment().run(100));
		System.out.println("JUnit: CounterTest повторяет протокол 25 раз, имеет @Timeout и завершает executor в finally.");
	}
}
