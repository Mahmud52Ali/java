package coursehub;

import coursehub.concurrent.ExecutorShutdown;
import coursehub.concurrent.NamedThreadFactory;
import coursehub.worker.VolatileStopWorker;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(8)
class StopWorkerTest {
	@Test
	void stopsAfterVolatileWrite() throws Exception {
		VolatileStopWorker worker = new VolatileStopWorker();
		var executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("test-worker"));
		try {
			var future = executor.submit(worker);
			assertTrue(worker.awaitStarted());
			worker.requestStop();
			future.get(2, TimeUnit.SECONDS);
			assertTrue(future.isDone());
		} finally {
			worker.requestStop();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
		assertTrue(executor.isTerminated());
	}

	@Test
	void supportsStopBeforeStart() {
		VolatileStopWorker worker = new VolatileStopWorker();
		worker.requestStop();
		worker.run();
		assertTrue(worker.iterations() == 0);
	}
}
