package coursehub.demo;

import coursehub.model.CourseDuration;
import java.util.List;

public final class Task03ReduceIdentity {
    private Task03ReduceIdentity() {
    }

    public static void run() {
        List<CourseDuration> durations = CourseExamples.catalog().stream()
                .map(course -> course.duration())
                .toList();

        CourseDuration sum = durations.stream().reduce(CourseDuration.ZERO, CourseDuration::plus);
        CourseDuration wrongIdentity = durations.stream()
                .reduce(new CourseDuration(1), CourseDuration::plus);
        CourseDuration emptyWithWrongIdentity = List.<CourseDuration>of().stream()
                .reduce(new CourseDuration(1), CourseDuration::plus);

        System.out.println("\nЗадание 3. reduce и identity");
        System.out.println("Сумма длительностей: " + sum);
        System.out.println("Неверная identity даёт: " + wrongIdentity);
        System.out.println("Для пустого списка неверная identity даёт: " + emptyWithWrongIdentity);
        System.out.println("plus ассоциативен: (a + b) + c = a + (b + c)");
    }
}
