package coursehub.demo;

import coursehub.model.Course;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class Task01DeterministicSorting {
    private Task01DeterministicSorting() {
    }

    public static void run() {
        List<Course> original = CourseExamples.catalog();
        List<Course> changedOrder = new ArrayList<>(original);
        java.util.Collections.reverse(changedOrder);

        Comparator<Course> order = Comparator
                .comparingInt((Course course) -> course.level().sortOrder())
                .thenComparingLong(course -> course.duration().hours())
                .thenComparing(Course::code)
                .thenComparingLong(Course::id);

        List<Course> first = original.stream().sorted(order).toList();
        List<Course> second = changedOrder.stream().sorted(order).toList();

        System.out.println("\nЗадание 1. Сортировка и детерминизм");
        System.out.println(first.stream().map(course -> course.code().value())
                .collect(Collectors.joining(", ")));
        System.out.println("Порядок совпал после изменения исходного списка: " + first.equals(second));
    }
}
