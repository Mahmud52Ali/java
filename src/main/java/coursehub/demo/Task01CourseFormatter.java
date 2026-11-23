package coursehub.demo;

import coursehub.format.CompactCourseFormatter;
import coursehub.format.CourseFormatter;
import coursehub.format.DetailedCourseFormatter;
import coursehub.model.Course;

public final class Task01CourseFormatter {
    private Task01CourseFormatter() {
    }

    public static void run() {
        System.out.println("\nЗадание 1. Один контракт, две реализации");
        Course course = new Course(101, "Java", 40);
        CourseFormatter compact = new CompactCourseFormatter();
        CourseFormatter detailed = new DetailedCourseFormatter();
        System.out.println(compact.format(course));
        System.out.println(detailed.format(course));
        System.out.println("Переменная знает CourseFormatter; реализация format выбирается по объекту во время выполнения.");
    }
}
