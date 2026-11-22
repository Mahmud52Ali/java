package coursehub.async.demo;

import coursehub.async.concurrent.ExecutorSupport;
import coursehub.async.concurrent.NamedThreadFactory;
import coursehub.async.service.BlockingIoSimulator;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class Task05ThreadExperiment {
    private static final int TASKS = 240;
    private static final int PLATFORM_POOL_SIZE = 24;
    private static final long BLOCKING_RESPONSE_MILLIS = 300;

    private Task05ThreadExperiment() {
    }

    public static void run() throws Exception {
        System.out.println("\nПрактическая работа 10. Задание 5. Platform и virtual threads");
        ScheduledExecutorService responses = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("experiment-response"));
        BlockingIoSimulator io = new BlockingIoSimulator(responses);
        try {
            ExperimentResult platform = runExperiment(
                    "platform", Executors.newFixedThreadPool(PLATFORM_POOL_SIZE,
                            new NamedThreadFactory("platform-io")), io, PLATFORM_POOL_SIZE);
            ExperimentResult virtual = runExperiment(
                    "virtual", Executors.newVirtualThreadPerTaskExecutor(), io, TASKS);
            print(platform);
            print(virtual);
            if (platform.platformThreads() != PLATFORM_POOL_SIZE
                    || virtual.virtualThreads() != TASKS || platform.virtualThreads() != 0
                    || virtual.platformThreads() != 0) {
                throw new IllegalStateException("Число task threads не соответствует конфигурации");
            }
            System.out.println("Наблюдение одного запуска; это не benchmark и heap delta приблизительна.");
            System.out.println("Платформенные потоки дефицитны для массового ожидания; virtual threads снижают цену ожидания,");
            System.out.println("но не ускоряют CPU-вычисления. Виртуальные потоки тоже занимают память и используют carriers.");
        } finally {
            ExecutorSupport.shutdown(responses, Duration.ofSeconds(2));
        }
    }

    private static ExperimentResult runExperiment(
            String kind, ExecutorService executor, BlockingIoSimulator io, int expectedConcurrent) throws Exception {
        Set<Long> platformThreadIds = ConcurrentHashMap.newKeySet();
        Set<Long> virtualThreadIds = ConcurrentHashMap.newKeySet();
        AtomicInteger active = new AtomicInteger();
        AtomicInteger peakActive = new AtomicInteger();
        CountDownLatch started = new CountDownLatch(expectedConcurrent);
        List<Future<?>> futures = new ArrayList<>();
        System.gc();
        long heapBefore = usedHeapBytes();
        long startedAt = System.nanoTime();
        try {
            for (int i = 0; i < TASKS; i++) {
                futures.add(executor.submit(() -> {
                    Thread thread = Thread.currentThread();
                    if (thread.isVirtual()) {
                        virtualThreadIds.add(thread.threadId());
                    } else {
                        platformThreadIds.add(thread.threadId());
                    }
                    int nowActive = active.incrementAndGet();
                    peakActive.accumulateAndGet(nowActive, Math::max);
                    started.countDown();
                    try {
                        return io.read("ok", BLOCKING_RESPONSE_MILLIS);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        return "cancelled";
                    } finally {
                        active.decrementAndGet();
                    }
                }));
            }
            if (!started.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Не набрано ожидаемое число одновременно запущенных задач");
            }
            System.gc();
            long heapDuring = usedHeapBytes();
            for (Future<?> future : futures) {
                future.get(15, TimeUnit.SECONDS);
            }
            long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
            return new ExperimentResult(kind, elapsedMillis, platformThreadIds.size(),
                    virtualThreadIds.size(), peakActive.get(), heapDuring - heapBefore);
        } finally {
            ExecutorSupport.shutdown(executor, Duration.ofSeconds(5));
        }
    }

    private static long usedHeapBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void print(ExperimentResult result) {
        System.out.println(result.kind() + ": " + TASKS + " задач, " + result.elapsedMillis() + " ms, "
                + "platform task threads=" + result.platformThreads() + ", virtual task threads="
                + result.virtualThreads() + ", peak active=" + result.peakActive()
                + ", approximate heap delta=" + result.heapDeltaBytes() + " bytes");
    }

    private record ExperimentResult(
            String kind, long elapsedMillis, int platformThreads, int virtualThreads,
            int peakActive, long heapDeltaBytes) {
    }
}
