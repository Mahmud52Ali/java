package coursehub.demo;

import coursehub.model.Course;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class Task05OptionalResult {
    private Task05OptionalResult() {
    }

    public static void run() {
        Comparator<Course> byDurationThenCode = Comparator
                .comparingLong((Course course) -> course.duration().hours())
                .thenComparing(Course::code);

        Optional<String> longest = CourseExamples.catalog().stream()
                .max(byDurationThenCode)
                .map(course -> course.code() + " — " + course.title());
        String result = longest.orElse("Каталог курсов пуст");

        Optional<String> emptyResult = List.<Course>of().stream()
                .max(byDurationThenCode)
                .map(course -> course.code() + " — " + course.title());

        System.out.println("\nЗадание 5. Optional как результат");
        System.out.println("Самый длинный курс: " + result);
        System.out.println("Резервный текст для пустого каталога: "
                + emptyResult.orElse("Каталог курсов пуст"));
    }
}
