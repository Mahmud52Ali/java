package coursehub.async.demo;

import coursehub.async.model.CourseCard;
import coursehub.async.model.Enrollment;
import coursehub.async.model.User;
import coursehub.async.service.AsyncCourseService;
import coursehub.async.concurrent.ExecutorSupport;
import coursehub.async.concurrent.NamedThreadFactory;
import coursehub.async.model.Course;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public final class Task02CompletablePipeline {
    private Task02CompletablePipeline() {
    }

    public static void run() throws Exception {
        System.out.println("\nПрактическая работа 10. Задание 2. Последовательное преобразование");
        AsyncCourseService service = new AsyncCourseService();
        ExecutorService ioExecutor = Executors.newFixedThreadPool(2, new NamedThreadFactory("course-io"));
        try {
            CountDownLatch continuationsAttached = new CountDownLatch(1);
            AtomicReference<CourseCard> displayedCard = new AtomicReference<>();
            CompletableFuture<Void> displayed = CompletableFuture
                    .supplyAsync(() -> {
                        System.out.println("Загрузка курса в " + Thread.currentThread().getName());
                        awaitRegistration(continuationsAttached);
                        return loadCourse(service, 101);
                    }, ioExecutor)
                    .thenApply(course -> {
                        System.out.println("Форматирование карточки в " + Thread.currentThread().getName());
                        return format(course);
                    })
                    .thenAccept(result -> {
                        displayedCard.set(result);
                        System.out.println("Вывод карточки в " + Thread.currentThread().getName() + ": " + result);
                    });
            continuationsAttached.countDown();
            displayed.get(2, TimeUnit.SECONDS);
            if (displayedCard.get() == null || displayedCard.get().courseId() != 101) {
                throw new IllegalStateException("Загружена неверная карточка курса");
            }

            CompletableFuture<User> user = CompletableFuture.supplyAsync(() -> {
                System.out.println("Загрузка пользователя в " + Thread.currentThread().getName());
                return service.loadUser(501);
            }, ioExecutor);
            CompletableFuture<List<Enrollment>> enrollments = user.thenComposeAsync(loadedUser -> {
                System.out.println("Запуск загрузки записей в " + Thread.currentThread().getName());
                return CompletableFuture.supplyAsync(() -> {
                    System.out.println("Получение записей в " + Thread.currentThread().getName());
                    return service.loadEnrollments(loadedUser);
                }, ioExecutor);
            }, ioExecutor);
            List<Enrollment> loadedEnrollments = enrollments.get(2, TimeUnit.SECONDS);
            if (loadedEnrollments.size() != 2) {
                throw new IllegalStateException("Не все записи пользователя загружены");
            }
            System.out.println("Записи пользователя: " + loadedEnrollments);
            System.out.println("thenApply вернул бы CompletableFuture<CompletableFuture<List<Enrollment>>>.");
            System.out.println("thenCompose возвращает плоский CompletableFuture<List<Enrollment>>.");
        } finally {
            ExecutorSupport.shutdown(ioExecutor, Duration.ofSeconds(2));
        }
    }

    private static void awaitRegistration(CountDownLatch continuationsAttached) {
        try {
            continuationsAttached.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new java.util.concurrent.CompletionException(exception);
        }
    }

    private static Course loadCourse(AsyncCourseService service, long courseId) {
        try {
            return service.loadCourse(courseId);
        } catch (java.io.IOException exception) {
            throw new java.util.concurrent.CompletionException(exception);
        }
    }

    private static CourseCard format(Course course) {
        return new CourseCard(course.id(), course.title(), course.durationHours() + " ч");
    }
}
