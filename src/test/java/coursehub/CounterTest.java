package coursehub;

import coursehub.counter.ControlledIncrementExperiment;
import coursehub.counter.Counter;
import coursehub.counter.CounterExperiment;
import coursehub.counter.SynchronizedBlockCounter;
import coursehub.counter.SynchronizedMethodCounter;
import java.util.stream.Stream;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Timeout(10)
class CounterTest {
	@RepeatedTest(25)
	void losesExactlyOneIncrementPerControlledRound() throws Exception {
		// Не просто общий старт: каждая пара чтений происходит до обеих записей.
		assertEquals(100, new ControlledIncrementExperiment().run(100));
	}

	static Stream<Counter> synchronizedCounters() {
		return Stream.of(new SynchronizedMethodCounter(), new SynchronizedBlockCounter());
	}

	@ParameterizedTest
	@MethodSource("synchronizedCounters")
	void synchronizedCounterKeepsEveryIncrement(Counter counter) throws Exception {
		assertEquals(200_000, CounterExperiment.run(counter, 8, 25_000));
	}
}
