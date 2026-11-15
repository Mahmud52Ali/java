package coursehub;

import coursehub.concurrent.ExecutorShutdown;
import coursehub.concurrent.NamedThreadFactory;
import coursehub.pipeline.Request;
import coursehub.pipeline.RequestPipeline;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(10)
class PipelineTest {
	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void processesEveryRequestExactlyOnce(boolean useExecutor) throws Exception {
		var names = ConcurrentHashMap.<String>newKeySet();
		RequestPipeline pipeline = new RequestPipeline(3, 2, 100, 1);
		var results = useExecutor
				? pipeline.runWithExecutor(request -> names.add(Thread.currentThread().getName()), Duration.ofSeconds(4))
				: pipeline.runWithThreads(request -> names.add(Thread.currentThread().getName()), Duration.ofSeconds(4));
		assertEquals(300, results.size());
		assertEquals(LongStream.rangeClosed(1, 300).boxed().toList(),
				results.stream().map(Request::id).sorted().toList());
		assertFalse(names.isEmpty());
		assertTrue(names.stream().allMatch(name -> name.startsWith(useExecutor ? "request-consumer-" : "raw-consumer-")));
		assertNoPipelineThreads();
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void emptyInputStillStopsAllConsumers(boolean useExecutor) throws Exception {
		RequestPipeline pipeline = new RequestPipeline(2, 3, 0, 1);
		List<Request> results = useExecutor
				? pipeline.runWithExecutor(request -> fail("Пустой ввод"), Duration.ofSeconds(2))
				: pipeline.runWithThreads(request -> fail("Пустой ввод"), Duration.ofSeconds(2));
		assertTrue(results.isEmpty());
		assertNoPipelineThreads();
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void handlerFailureDoesNotLeaveBlockedProducers(boolean useExecutor) {
		RequestPipeline pipeline = new RequestPipeline(2, 1, 100, 1);
		assertThrows(Exception.class, () -> {
			if (useExecutor) {
				pipeline.runWithExecutor(request -> { throw new IOException("Ошибка обработки"); }, Duration.ofMillis(300));
			} else {
				pipeline.runWithThreads(request -> { throw new IOException("Ошибка обработки"); }, Duration.ofMillis(300));
			}
		});
		assertNoPipelineThreads();
	}

	@Test
	void fullQueueBlocksPutUntilConsumerTakesItem() throws Exception {
		var executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("test-backpressure"));
		var queue = new ArrayBlockingQueue<Integer>(1);
		CountDownLatch aboutToPut = new CountDownLatch(1);
		queue.put(1);
		try {
			var producer = executor.submit(() -> {
				aboutToPut.countDown();
				queue.put(2);
				return null;
			});
			assertTrue(aboutToPut.await(2, TimeUnit.SECONDS));
			assertFalse(producer.isDone());
			assertEquals(1, queue.take());
			producer.get(2, TimeUnit.SECONDS);
			assertEquals(2, queue.take());
		} finally {
			executor.shutdownNow();
			ExecutorShutdown.shutdown(executor, Duration.ofSeconds(1));
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void cancellationInterruptsBlockedPipeline(boolean useExecutor) throws Exception {
		var coordinator = Executors.newSingleThreadExecutor(new NamedThreadFactory("test-coordinator"));
		CountDownLatch processing = new CountDownLatch(1);
		CountDownLatch release = new CountDownLatch(1);
		RequestPipeline pipeline = new RequestPipeline(2, 2, 100, 1);
		try {
			var future = coordinator.submit(() -> {
				if (useExecutor) {
					return pipeline.runWithExecutor(request -> {
						processing.countDown();
						release.await();
					}, Duration.ofSeconds(4));
				}
				return pipeline.runWithThreads(request -> {
					processing.countDown();
					release.await();
				}, Duration.ofSeconds(4));
			});
			assertTrue(processing.await(2, TimeUnit.SECONDS));
			assertTrue(future.cancel(true));
		} finally {
			release.countDown();
			ExecutorShutdown.shutdown(coordinator, Duration.ofSeconds(3));
		}
		assertTrue(coordinator.isTerminated());
		assertNoPipelineThreads();
	}

	private static void assertNoPipelineThreads() {
		assertTrue(Thread.getAllStackTraces().keySet().stream().noneMatch(thread -> thread.isAlive()
				&& (thread.getName().startsWith("raw-producer-") || thread.getName().startsWith("raw-consumer-")
				|| thread.getName().startsWith("request-producer-") || thread.getName().startsWith("request-consumer-"))));
	}
}
