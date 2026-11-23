package coursehub.demo;

import coursehub.format.CompactCourseFormatter;
import coursehub.format.CourseFormatter;
import coursehub.format.DetailedCourseFormatter;
import coursehub.format.JsonCourseFormatter;
import coursehub.model.Course;

public final class Task03FormatterArray {
    private Task03FormatterArray() {
    }

    public static void run() {
        System.out.println("\nЗадание 3. Полиморфный массив");
        Course course = new Course(202, "Databases", 32);
        CourseFormatter[] formatters = {
                new CompactCourseFormatter(),
                new DetailedCourseFormatter(),
                new JsonCourseFormatter()
        };
        for (CourseFormatter formatter : formatters) {
            System.out.println(formatter.format(course));
        }
        System.out.println("Обход зависит только от CourseFormatter и не проверяет тип реализации.");
    }
}
