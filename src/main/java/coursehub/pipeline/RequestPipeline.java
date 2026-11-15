package coursehub.pipeline;

import coursehub.concurrent.ExecutorShutdown;
import coursehub.concurrent.NamedThreadFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class RequestPipeline {
	private static final Request STOP = new Request(0);
	private final int producerCount;
	private final int consumerCount;
	private final int requestsPerProducer;
	private final int capacity;

	public RequestPipeline(int producerCount, int consumerCount, int requestsPerProducer, int capacity) {
		if (producerCount < 1 || consumerCount < 1 || requestsPerProducer < 0 || capacity < 1) {
			throw new IllegalArgumentException("Некорректные параметры конвейера");
		}
		this.producerCount = producerCount;
		this.consumerCount = consumerCount;
		this.requestsPerProducer = requestsPerProducer;
		this.capacity = capacity;
	}

	// Пулы разделены: ожидающий producer не занимает место необходимого consumer.
	public List<Request> runWithExecutor(RequestHandler handler, Duration timeout) throws Exception {
		Objects.requireNonNull(handler, "Нужен обработчик");
		Deadline deadline = new Deadline(timeout);
		BlockingQueue<Request> queue = new ArrayBlockingQueue<>(capacity);
		ConcurrentLinkedQueue<Request> processed = new ConcurrentLinkedQueue<>();
		var producers = Executors.newFixedThreadPool(producerCount, new NamedThreadFactory("request-producer"));
		var consumers = Executors.newFixedThreadPool(consumerCount, new NamedThreadFactory("request-consumer"));
		List<Future<?>> all = new ArrayList<>();
		try {
			List<Future<?>> consumerResults = new ArrayList<>();
			for (int i = 0; i < consumerCount; i++) {
				var future = consumers.submit(() -> consume(queue, processed, handler));
				consumerResults.add(future);
				all.add(future);
			}
			List<Future<?>> producerResults = new ArrayList<>();
			for (int i = 0; i < producerCount; i++) {
				int producerId = i;
				var future = producers.submit(() -> produce(queue, producerId));
				producerResults.add(future);
				all.add(future);
			}
			await(producerResults, deadline);
			sendStopSignals(queue, deadline);
			await(consumerResults, deadline);
			return List.copyOf(processed);
		} finally {
			all.forEach(result -> result.cancel(true));
			// Оба shutdown выполняются даже при ошибке завершения первого пула.
			try {
				ExecutorShutdown.shutdown(producers, Duration.ofSeconds(1));
			} finally {
				ExecutorShutdown.shutdown(consumers, Duration.ofSeconds(1));
			}
		}
	}

	public List<Request> runWithThreads(RequestHandler handler, Duration timeout) throws Exception {
		Objects.requireNonNull(handler, "Нужен обработчик");
		Deadline deadline = new Deadline(timeout);
		BlockingQueue<Request> queue = new ArrayBlockingQueue<>(capacity);
		ConcurrentLinkedQueue<Request> processed = new ConcurrentLinkedQueue<>();
		ConcurrentLinkedQueue<Exception> failures = new ConcurrentLinkedQueue<>();
		List<Thread> all = new ArrayList<>();
		try {
			List<Thread> consumers = new ArrayList<>();
			for (int i = 0; i < consumerCount; i++) {
				Thread thread = startThread("raw-consumer-" + (i + 1),
						() -> consume(queue, processed, handler), failures);
				consumers.add(thread);
				all.add(thread);
			}
			List<Thread> producers = new ArrayList<>();
			for (int i = 0; i < producerCount; i++) {
				int producerId = i;
				Thread thread = startThread("raw-producer-" + (i + 1), () -> produce(queue, producerId), failures);
				producers.add(thread);
				all.add(thread);
			}
			join(producers, deadline);
			checkFailures(failures);
			sendStopSignals(queue, deadline);
			join(consumers, deadline);
			checkFailures(failures);
			return List.copyOf(processed);
		} finally {
			all.forEach(Thread::interrupt);
			join(all, new Deadline(Duration.ofSeconds(2)));
		}
	}

	private Void produce(BlockingQueue<Request> queue, int producerId) throws InterruptedException {
		for (int i = 1; i <= requestsPerProducer; i++) {
			queue.put(new Request((long) producerId * requestsPerProducer + i));
		}
		return null;
	}

	private static Void consume(BlockingQueue<Request> queue, ConcurrentLinkedQueue<Request> processed,
			RequestHandler handler) throws Exception {
		while (true) {
			Request request = queue.take();
			if (request == STOP) {
				return null;
			}
			handler.process(request);
			processed.add(request);
		}
	}

	// После завершения всех producers FIFO ставит poison pills за всеми заявками.
	private void sendStopSignals(BlockingQueue<Request> queue, Deadline deadline) throws Exception {
		for (int i = 0; i < consumerCount; i++) {
			if (!queue.offer(STOP, deadline.remainingNanos(), TimeUnit.NANOSECONDS)) {
				throw new TimeoutException("Не удалось отправить сигнал завершения");
			}
		}
	}

	private static void await(List<Future<?>> futures, Deadline deadline) throws Exception {
		for (Future<?> future : futures) {
			future.get(deadline.remainingNanos(), TimeUnit.NANOSECONDS);
		}
	}

	private static Thread startThread(String name, Callable<Void> action,
			ConcurrentLinkedQueue<Exception> failures) {
		Thread thread = new Thread(() -> {
			try {
				action.call();
			} catch (InterruptedException exception) {
				Thread.currentThread().interrupt();
				failures.add(exception);
			} catch (Exception exception) {
				failures.add(exception);
			}
		}, name);
		thread.start();
		return thread;
	}

	private static void join(List<Thread> threads, Deadline deadline) throws Exception {
		for (Thread thread : threads) {
			if (thread.isAlive()) {
				TimeUnit.NANOSECONDS.timedJoin(thread, deadline.remainingNanos());
				if (thread.isAlive()) {
					throw new TimeoutException("Поток не завершился: " + thread.getName());
				}
			}
		}
	}

	private static void checkFailures(ConcurrentLinkedQueue<Exception> failures) throws Exception {
		if (!failures.isEmpty()) {
			throw failures.peek();
		}
	}
}
