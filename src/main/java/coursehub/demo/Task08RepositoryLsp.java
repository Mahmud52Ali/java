package coursehub.demo;

import coursehub.model.Course;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Task08RepositoryLsp {
    private Task08RepositoryLsp() {
    }

    public static void run() {
        System.out.println("\nЗадание 8. Нарушение LSP");
        Course course = new Course(808, "Architecture", 30);
        CourseRepository broken = new ReadOnlyCourseRepository();
        try {
            saveForClient(broken, course);
        } catch (UnsupportedOperationException exception) {
            System.out.println("Старый контракт обещал save, но read-only объект бросил исключение.");
        }

        CourseCatalog catalog = new CourseCatalog();
        catalog.save(course);
        CourseReader readOnlyView = new ReadOnlyCourseView(catalog);
        System.out.println("Split interface lookup: " + readOnlyView.findById(808).orElseThrow().title());
        System.out.println("Read-only клиент получает CourseReader и не видит метода save.");
    }

    private static void saveForClient(CourseRepository repository, Course course) {
        repository.save(course);
    }

    private interface CourseRepository {
        Optional<Course> findById(long id);

        void save(Course course);
    }

    private static class ReadOnlyCourseRepository implements CourseRepository {
        @Override
        public Optional<Course> findById(long id) {
            return Optional.empty();
        }

        @Override
        public void save(Course course) {
            throw new UnsupportedOperationException("Read only");
        }
    }

    private interface CourseReader {
        Optional<Course> findById(long id);
    }

    private interface CourseWriter extends CourseReader {
        void save(Course course);
    }

    private static class CourseCatalog implements CourseWriter {
        private final Map<Long, Course> courses = new HashMap<>();

        @Override
        public Optional<Course> findById(long id) {
            return Optional.ofNullable(courses.get(id));
        }

        @Override
        public void save(Course course) {
            courses.put(course.id(), course);
        }
    }

    private static class ReadOnlyCourseView implements CourseReader {
        private final CourseReader source;

        private ReadOnlyCourseView(CourseReader source) {
            this.source = source;
        }

        @Override
        public Optional<Course> findById(long id) {
            return source.findById(id);
        }
    }
}
