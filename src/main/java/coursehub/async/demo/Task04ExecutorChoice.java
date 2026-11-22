package coursehub.async.demo;

import coursehub.async.concurrent.ExecutorSupport;
import coursehub.async.concurrent.NamedThreadFactory;
import coursehub.async.service.BlockingIoSimulator;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Task04ExecutorChoice {
    private Task04ExecutorChoice() {
    }

    public static void run() throws Exception {
        System.out.println("\nПрактическая работа 10. Задание 4. Правильный executor");
        int cpuThreads = Math.max(1, Runtime.getRuntime().availableProcessors());
        ExecutorService cpuExecutor = Executors.newFixedThreadPool(cpuThreads, new NamedThreadFactory("cpu"));
        ExecutorService ioExecutor = Executors.newFixedThreadPool(8, new NamedThreadFactory("blocking-io"));
        ScheduledExecutorService responses = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("io-response"));
        BlockingIoSimulator io = new BlockingIoSimulator(responses);
        try {
            Future<Integer> primeCount = cpuExecutor.submit(() -> countPrimes(60_000));
            Future<String> blockingResult = ioExecutor.submit(() -> io.read("Ответ fixed I/O pool", 80));
            System.out.println("CPU pool: " + cpuThreads + " поток(ов), простых чисел: "
                    + primeCount.get(3, TimeUnit.SECONDS));
            System.out.println("I/O pool: до 8 блокирующих операций, результат: "
                    + blockingResult.get(3, TimeUnit.SECONDS));
            demonstrateStarvation();
            demonstrateVirtualThreads(io);
        } finally {
            ExecutorSupport.shutdown(responses, Duration.ofSeconds(2));
            ExecutorSupport.shutdown(ioExecutor, Duration.ofSeconds(2));
            ExecutorSupport.shutdown(cpuExecutor, Duration.ofSeconds(2));
        }
    }

    private static void demonstrateStarvation() throws Exception {
        ExecutorService oneWorker = Executors.newSingleThreadExecutor(new NamedThreadFactory("starvation"));
        CountDownLatch childRan = new CountDownLatch(1);
        try {
            Future<Boolean> parent = oneWorker.submit(() -> {
                oneWorker.submit(childRan::countDown);
                return childRan.await(150, TimeUnit.MILLISECONDS);
            });
            boolean childCompletedBeforeParent = parent.get(1, TimeUnit.SECONDS);
            if (childCompletedBeforeParent) {
                throw new IllegalStateException("Тест starvation не воспроизвёл ожидание");
            }
            boolean childCompletedAfterParentReleasedWorker = childRan.await(1, TimeUnit.SECONDS);
            System.out.println("Starvation: дочерняя задача ждала единственный занятый worker; после освобождения выполнилась: "
                    + childCompletedAfterParentReleasedWorker);
        } finally {
            ExecutorSupport.shutdown(oneWorker, Duration.ofSeconds(2));
        }
    }

    private static void demonstrateVirtualThreads(BlockingIoSimulator io) throws Exception {
        try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> responses = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                int requestId = i;
                responses.add(virtualExecutor.submit(() -> {
                    if (!Thread.currentThread().isVirtual()) {
                        throw new IllegalStateException("Запрос запущен не на virtual thread");
                    }
                    return io.read("запрос-" + requestId, 40);
                }));
            }
            for (Future<String> response : responses) {
                response.get(2, TimeUnit.SECONDS);
            }
            System.out.println("Virtual-thread executor обработал 12 блокирующих запросов.");
        }
    }

    private static int countPrimes(int limit) {
        int count = 0;
        for (int value = 2; value <= limit; value++) {
            boolean prime = true;
            for (int divisor = 2; divisor * divisor <= value; divisor++) {
                if (value % divisor == 0) {
                    prime = false;
                    break;
                }
            }
            if (prime) {
                count++;
            }
        }
        return count;
    }
}
