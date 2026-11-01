package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseCatalogSummary;
import coursehub.model.CourseLevel;
import coursehub.model.LevelSummary;
import java.util.EnumMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

public final class Task04CollectorsSummary {
    private Task04CollectorsSummary() {
    }

    public static void run() {
        List<Course> courses = CourseExamples.catalog();
        Map<CourseLevel, LongSummaryStatistics> collected = courses.stream()
                .collect(Collectors.groupingBy(
                        Course::level,
                        () -> new EnumMap<>(CourseLevel.class),
                        Collectors.summarizingLong(course -> course.duration().hours())
                ));

        Map<CourseLevel, LevelSummary> levels = new EnumMap<>(CourseLevel.class);
        collected.forEach((level, stats) ->
                levels.put(level, new LevelSummary(stats.getCount(), stats.getAverage())));
        CourseCatalogSummary summary = new CourseCatalogSummary(levels);

        System.out.println("\nЗадание 4. Collectors");
        summary.byLevel().forEach((level, stats) -> System.out.printf(
                "%s: курсов %d, средняя длительность %.1f ч%n",
                level.title(), stats.count(), stats.averageDurationHours()));
        System.out.println("Входной каталог сгруппирован за один проход.");
    }
}
