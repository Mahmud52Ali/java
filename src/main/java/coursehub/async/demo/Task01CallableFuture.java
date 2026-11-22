package coursehub.async.demo;

import coursehub.async.concurrent.ExecutorSupport;
import coursehub.async.concurrent.NamedThreadFactory;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task01CallableFuture {
    private Task01CallableFuture() {
    }

    public static void run() throws Exception {
        System.out.println("\nПрактическая работа 10. Задание 1. Callable и Future");
        ExecutorService executor = Executors.newFixedThreadPool(2, new NamedThreadFactory("future"));
        try {
            AtomicInteger capturedResult = new AtomicInteger();
            Future<?> runnableResult = executor.submit(() -> capturedResult.set(21));
            runnableResult.get(1, TimeUnit.SECONDS);
            System.out.println("Runnable: прямого результата нет, внешнее значение = " + capturedResult.get());
            System.out.println("Runnable.run не объявляет checked exception; Callable.call может его выбросить.");

            Future<Integer> callableResult = executor.submit((Callable<Integer>) () -> 21 * 2);
            int result = callableResult.get(1, TimeUnit.SECONDS);
            if (result != 42) {
                throw new IllegalStateException("Callable вернул неожиданный результат");
            }
            System.out.println("Callable вернул: " + result);

            Future<Integer> failed = executor.submit((Callable<Integer>) () -> {
                throw new IOException("Ошибка чтения курса");
            });
            try {
                failed.get(1, TimeUnit.SECONDS);
            } catch (ExecutionException exception) {
                System.out.println("Ошибка вычисления: " + exception.getCause().getMessage());
            }

            CountDownLatch started = new CountDownLatch(1);
            CountDownLatch stopped = new CountDownLatch(1);
            Future<String> longTask = executor.submit(() -> {
                started.countDown();
                try (DemoResource resource = new DemoResource()) {
                    try {
                        new CountDownLatch(1).await();
                        return "Задача завершилась сама";
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        System.out.println("Получен interrupt; флаг восстановлен: "
                                + Thread.currentThread().isInterrupted());
                        return "Задача остановилась по запросу отмены";
                    }
                } finally {
                    stopped.countDown();
                }
            });

            if (!started.await(1, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Долгая задача не запустилась");
            }
            try {
                longTask.get(100, TimeUnit.MILLISECONDS);
            } catch (TimeoutException exception) {
                System.out.println("Timeout ожидания результата; это не ошибка вычисления");
                System.out.println("cancel(true): " + longTask.cancel(true));
            }
            System.out.println("Задача и ресурс освобождены: " + stopped.await(1, TimeUnit.SECONDS));
            System.out.println("interrupt просит поток завершиться; код должен его обработать.");
        } finally {
            ExecutorSupport.shutdown(executor, Duration.ofSeconds(2));
        }
    }

    private static final class DemoResource implements AutoCloseable {
        @Override
        public void close() {
            System.out.println("Ресурс освобождён");
        }
    }
}
