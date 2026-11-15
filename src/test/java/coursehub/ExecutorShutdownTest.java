package coursehub;

import coursehub.concurrent.ExecutorShutdown;
import coursehub.concurrent.NamedThreadFactory;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(8)
class ExecutorShutdownTest {
	@Test
	void gracefulShutdownFinishesAcceptedTasks() throws Exception {
		var executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("test-graceful"));
		try {
			var result = executor.submit(() -> 42);
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
			assertEquals(42, result.get(1, TimeUnit.SECONDS));
			assertTrue(executor.isTerminated());
		} finally {
			executor.shutdownNow();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
	}

	@Test
	void timeoutEscalatesToShutdownNow() throws Exception {
		var executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("test-forced"));
		CountDownLatch started = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		AtomicBoolean interrupted = new AtomicBoolean();
		try {
			executor.submit(() -> {
				started.countDown();
				try {
					release.await();
				} catch (InterruptedException exception) {
					interrupted.set(true);
					Thread.currentThread().interrupt();
				}
			});
			assertTrue(started.await(2, TimeUnit.SECONDS));
			ExecutorShutdown.shutdown(executor, Duration.ofMillis(100));
			assertTrue(interrupted.get());
			assertTrue(executor.isTerminated());
		} finally {
			release.countDown();
			executor.shutdownNow();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
	}

	@Test
	void restoresCallerInterruptFlag() throws Exception {
		var executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("test-interrupt"));
		CountDownLatch started = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		try {
			executor.submit(() -> {
				started.countDown();
				release.await();
				return null;
			});
			assertTrue(started.await(2, TimeUnit.SECONDS));
			Thread.currentThread().interrupt();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
			assertTrue(Thread.currentThread().isInterrupted());
		} finally {
			Thread.interrupted();
			release.countDown();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
		assertTrue(executor.isTerminated());
	}
}
