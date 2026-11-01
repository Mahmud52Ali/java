package coursehub.demo;

import coursehub.model.EnrollmentEvent;
import coursehub.report.CourseHubReport;
import coursehub.service.CourseHubAnalyzer;
import java.util.List;

public final class Task07OnePassReport {
    private Task07OnePassReport() {
    }

    public static void run() {
        List<EnrollmentEvent> input = List.of(
                new EnrollmentEvent(1, 101, true),
                new EnrollmentEvent(2, 101, false),
                new EnrollmentEvent(1, 102, true),
                new EnrollmentEvent(3, 102, true),
                new EnrollmentEvent(4, 103, false),
                new EnrollmentEvent(4, 103, false),
                new EnrollmentEvent(5, 104, true),
                new EnrollmentEvent(0, 102, true),
                new EnrollmentEvent(6, 999, true),
                new EnrollmentEvent(7, 105, false)
        );

        CourseHubReport report = new CourseHubAnalyzer().analyze(
                CourseExamples.catalog(), input.stream());

        System.out.println("\nЗадание 7. Однопроходный отчёт CourseHub");
        System.out.println("Уникальных студентов: " + report.uniqueStudentCount());
        report.progressByCourse().values().forEach(progress -> System.out.printf(
                "%s: записей %d, завершено %d, completion rate %.1f%%%n",
                progress.courseCode(), progress.enrollmentCount(), progress.completionCount(),
                progress.completionRatePercent()));
        System.out.println("Top-3 по числу записей: " + report.topThreeByEnrollment());
        System.out.println("Распределение курсов по уровню: " + report.courseCountByLevel());
        System.out.println("Всего аномалий: " + report.anomalyCount() + " " + report.anomaliesByType());
        System.out.println("Сохранённые примеры аномалий: " + report.anomalySamples());
        System.out.println("Источник обработан одним проходом; события не копировались в список.");
    }
}
