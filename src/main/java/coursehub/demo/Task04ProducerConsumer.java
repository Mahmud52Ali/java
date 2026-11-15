package coursehub.demo;

import coursehub.pipeline.PoolSizing;
import coursehub.pipeline.Request;
import coursehub.pipeline.RequestPipeline;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public final class Task04ProducerConsumer {
	private Task04ProducerConsumer() {
	}

	public static void run() throws Exception {
		System.out.println("\nЗадание 4. Producer-consumer");
		int processors = Runtime.getRuntime().availableProcessors();
		int consumers = PoolSizing.cpuBound(processors, 2);
		RequestPipeline pipeline = new RequestPipeline(3, consumers, 100, 8);
		AtomicLong checksum = new AtomicLong();
		var threads = pipeline.runWithThreads(request -> checksum.addAndGet(cpuWork(request)), Duration.ofSeconds(10));
		var executor = pipeline.runWithExecutor(request -> checksum.addAndGet(cpuWork(request)), Duration.ofSeconds(10));
		System.out.println("Сценарий CPU-bound, ядер=" + processors + ", consumers=" + consumers + ", producers=3, capacity=8");
		System.out.println("Thread: обработано=" + threads.size() + "; ExecutorService: обработано=" + executor.size());
		System.out.println("Контрольная сумма CPU-обработки=" + checksum.get());
	}

	private static long cpuWork(Request request) {
		long value = request.id();
		for (int i = 0; i < 10_000; i++) {
			value = value * 1_664_525 + 1_013_904_223;
		}
		return value;
	}
}
