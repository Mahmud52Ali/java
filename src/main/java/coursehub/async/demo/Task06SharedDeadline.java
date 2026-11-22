package coursehub.async.demo;

import coursehub.async.concurrent.ExecutorSupport;
import coursehub.async.concurrent.NamedThreadFactory;
import coursehub.async.service.BlockingIoSimulator;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Task06SharedDeadline {
    private static final long BUDGET_MILLIS = 800;

    private Task06SharedDeadline() {
    }

    public static void run() throws Exception {
        System.out.println("\nПрактическая работа 10. Задание 6. Общий deadline");
        ExecutorService workers = Executors.newFixedThreadPool(3, new NamedThreadFactory("deadline-worker"));
        ScheduledExecutorService responses = Executors.newSingleThreadScheduledExecutor(
                new NamedThreadFactory("deadline-response"));
        BlockingIoSimulator io = new BlockingIoSimulator(responses);
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(BUDGET_MILLIS);
        CountDownLatch courseDone = new CountDownLatch(1);
        CountDownLatch enrollmentDone = new CountDownLatch(1);
        CountDownLatch recommendationStarted = new CountDownLatch(1);
        CountDownLatch recommendationDone = new CountDownLatch(1);
        Future<String> course = submit(workers, io, "карточка курса", 120,
                new CountDownLatch(0), courseDone);
        Future<String> enrollments = submit(workers, io, "записи студента", 220,
                new CountDownLatch(0), enrollmentDone);
        Future<String> recommendation = submit(workers, io, "необязательные рекомендации", 3_000,
                recommendationStarted, recommendationDone);
        try {
            awaitStage(recommendationStarted, deadline, "запуск необязательной задачи");
            String courseResult = getStage(course, deadline, "карточка курса");
            String enrollmentResult = getStage(enrollments, deadline, "записи студента");
            System.out.println("Получены обязательные результаты: " + courseResult + "; " + enrollmentResult);
            System.out.println("Остаток общего бюджета: " + remainingMillis(deadline) + " ms");

            boolean cancelled = recommendation.cancel(true);
            boolean stopped = awaitStage(recommendationDone, deadline, "завершение отменённой задачи");
            if (!cancelled || !stopped) {
                throw new IllegalStateException("Отменённая подзадача не завершилась корректно");
            }
            System.out.println("Отменена больше не нужная работа; завершение подтверждено: " + stopped);
        } catch (TimeoutException exception) {
            course.cancel(true);
            enrollments.cancel(true);
            recommendation.cancel(true);
            System.out.println("Общий deadline исчерпан: " + exception.getMessage());
            awaitCleanup(courseDone, enrollmentDone, recommendationDone);
        } catch (ExecutionException exception) {
            course.cancel(true);
            enrollments.cancel(true);
            recommendation.cancel(true);
            System.out.println("Стадия завершилась ошибкой: " + exception.getCause());
            awaitCleanup(courseDone, enrollmentDone, recommendationDone);
        } finally {
            ExecutorSupport.shutdown(responses, Duration.ofSeconds(2));
            ExecutorSupport.shutdown(workers, Duration.ofSeconds(2));
        }
        System.out.println("Structured concurrency решает проблему группы несвязанных задач: общий родительский scope");
        System.out.println("может ждать дочерние задачи, отменять их вместе и связывать ошибки с вызвавшей операцией.");
        System.out.println("Здесь использованы обычные Future и deadline; preview API не включён.");
    }

    private static Future<String> submit(
            ExecutorService workers, BlockingIoSimulator io, String label, long responseDelayMillis,
            CountDownLatch started, CountDownLatch done) {
        return workers.submit(() -> {
            started.countDown();
            try {
                return io.read(label, responseDelayMillis);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw exception;
            } finally {
                done.countDown();
            }
        });
    }

    private static String getStage(Future<String> future, long deadline, String stage)
            throws InterruptedException, ExecutionException, TimeoutException {
        long remaining = remainingNanos(deadline);
        System.out.println("Ожидание " + stage + ", остаток "
                + TimeUnit.NANOSECONDS.toMillis(remaining) + " ms");
        return future.get(remaining, TimeUnit.NANOSECONDS);
    }

    private static boolean awaitStage(CountDownLatch latch, long deadline, String stage)
            throws InterruptedException, TimeoutException {
        long remaining = remainingNanos(deadline);
        System.out.println("Ожидание " + stage + ", остаток "
                + TimeUnit.NANOSECONDS.toMillis(remaining) + " ms");
        if (!latch.await(remaining, TimeUnit.NANOSECONDS)) {
            throw new TimeoutException("таймаут на стадии " + stage);
        }
        return true;
    }

    private static long remainingNanos(long deadline) throws TimeoutException {
        long remaining = deadline - System.nanoTime();
        if (remaining <= 0) {
            throw new TimeoutException("временной бюджет исчерпан");
        }
        return remaining;
    }

    private static long remainingMillis(long deadline) throws TimeoutException {
        return TimeUnit.NANOSECONDS.toMillis(remainingNanos(deadline));
    }

    private static void awaitCleanup(CountDownLatch... tasks) throws InterruptedException {
        long cleanupDeadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
        for (CountDownLatch task : tasks) {
            long remaining = cleanupDeadline - System.nanoTime();
            if (remaining <= 0 || !task.await(remaining, TimeUnit.NANOSECONDS)) {
                System.out.println("Задача не подтвердила завершение в cleanup-бюджет");
                return;
            }
        }
    }
}
