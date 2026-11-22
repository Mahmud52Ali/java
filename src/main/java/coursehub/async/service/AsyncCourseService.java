package coursehub.async.service;

import coursehub.async.model.Course;
import coursehub.async.model.Enrollment;
import coursehub.async.model.User;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public final class AsyncCourseService {
    private final Map<Long, Course> courses = Map.of(
            101L, new Course(101, "JAVA-101", "Основы Java", 40),
            102L, new Course(102, "JAVA-102", "Коллекции Java", 48),
            103L, new Course(103, "JAVA-103", "Параллельное программирование", 60)
    );

    public Course loadCourse(long courseId) throws IOException {
        Course course = courses.get(courseId);
        if (course == null) {
            throw new IOException("Курс с ID " + courseId + " не найден");
        }
        return course;
    }

    public CompletableFuture<Course> loadCourseAsync(long courseId, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return loadCourse(courseId);
            } catch (IOException exception) {
                throw new CompletionException(exception);
            }
        }, executor);
    }

    public User loadUser(long userId) {
        return new User(userId, "Дмитрий Горбачев");
    }

    public List<Enrollment> loadEnrollments(User user) {
        return List.of(
                new Enrollment(user.id(), 101, "активна"),
                new Enrollment(user.id(), 102, "завершена")
        );
    }
}
