package coursehub.async.demo;

import coursehub.async.model.LoadOutcome;
import coursehub.async.service.AsyncCourseService;
import coursehub.async.concurrent.ExecutorSupport;
import coursehub.async.concurrent.NamedThreadFactory;
import coursehub.async.model.Course;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task03AllOfResults {
    private Task03AllOfResults() {
    }

    public static void run() throws Exception {
        System.out.println("\nПрактическая работа 10. Задание 3. allOf и результаты");
        AsyncCourseService service = new AsyncCourseService();
        ExecutorService ioExecutor = Executors.newFixedThreadPool(3, new NamedThreadFactory("batch-load"));
        List<Long> courseIds = List.of(101L, 999L, 102L);
        try {
            List<CompletableFuture<Course>> failFastLoads = courseIds.stream()
                    .map(id -> service.loadCourseAsync(id, ioExecutor))
                    .toList();
            CompletableFuture<List<Course>> failFast = failFast(failFastLoads);
            try {
                System.out.println("Fail-fast результат: " + failFast.get(2, TimeUnit.SECONDS));
            } catch (ExecutionException exception) {
                if (!rootCause(exception).getMessage().contains("999")) {
                    throw new IllegalStateException("Fail-fast вернул неожиданную ошибку", exception);
                }
                System.out.println("Fail-fast завершился ошибкой: " + rootCause(exception).getMessage());
            }

            List<CompletableFuture<LoadOutcome>> partialLoads = courseIds.stream()
                    .map(id -> service.loadCourseAsync(id, ioExecutor)
                            .handle((course, error) -> new LoadOutcome(id, course,
                                    error == null ? null : message(rootCause(error)))))
                    .toList();
            CompletableFuture<?>[] all = partialLoads.toArray(new CompletableFuture<?>[0]);
            List<LoadOutcome> partial = CompletableFuture.allOf(all)
                    .thenApply(ignored -> partialLoads.stream().map(CompletableFuture::join).toList())
                    .get(2, TimeUnit.SECONDS);
            if (!partial.stream().map(LoadOutcome::courseId).toList().equals(courseIds)
                    || partial.stream().filter(LoadOutcome::succeeded).count() != 2) {
                throw new IllegalStateException("Частичный результат потерял порядок или успешные загрузки");
            }
            System.out.println("Частичный результат в исходном порядке:");
            partial.forEach(outcome -> System.out.println("  " + outcome));
        } finally {
            ExecutorSupport.shutdown(ioExecutor, Duration.ofSeconds(2));
        }
    }

    private static CompletableFuture<List<Course>> failFast(List<CompletableFuture<Course>> loads) {
        CompletableFuture<List<Course>> result = new CompletableFuture<>();
        if (loads.isEmpty()) {
            result.complete(List.of());
            return result;
        }
        AtomicInteger successes = new AtomicInteger();
        for (CompletableFuture<Course> load : loads) {
            load.whenComplete((course, error) -> {
                if (error != null) {
                    result.completeExceptionally(rootCause(error));
                } else if (successes.incrementAndGet() == loads.size()) {
                    result.complete(loads.stream().map(CompletableFuture::join).toList());
                }
            });
        }
        return result;
    }

    private static Throwable rootCause(Throwable error) {
        Throwable result = error;
        while (result instanceof CompletionException && result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }

    private static String message(Throwable error) {
        return error.getClass().getSimpleName() + ": " + error.getMessage();
    }
}
