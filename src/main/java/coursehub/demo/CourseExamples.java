package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseCode;
import coursehub.model.CourseDuration;
import coursehub.model.CourseLevel;
import java.util.List;

public final class CourseExamples {
    private CourseExamples() {
    }

    public static List<Course> catalog() {
        return List.of(
                new Course(101, new CourseCode("JAVA-101"), "Основы Java", CourseLevel.BEGINNER,
                        new CourseDuration(30), List.of("Переменные", "ООП", "Коллекции")),
                new Course(102, new CourseCode("JAVA-102"), "Java Core", CourseLevel.INTERMEDIATE,
                        new CourseDuration(60), List.of("ООП", "Streams", "Generics")),
                new Course(103, new CourseCode("SQL-101"), "SQL и базы данных", CourseLevel.BEGINNER,
                        new CourseDuration(24), List.of("SQL", "JOIN", "Транзакции")),
                new Course(104, new CourseCode("WEB-101"), "Веб-разработка", CourseLevel.ADVANCED,
                        new CourseDuration(60), List.of("HTTP", "REST", "SQL")),
                new Course(105, new CourseCode("JAVA-STREAMS"), "Java Streams", CourseLevel.INTERMEDIATE,
                        new CourseDuration(24), List.of("Streams", "Лямбда", "  ООП  "))
        );
    }
}
