package coursehub.service;

import coursehub.model.Course;
import coursehub.model.CourseCode;
import coursehub.model.CourseLevel;
import coursehub.model.EnrollmentEvent;
import coursehub.model.Registration;
import coursehub.report.CourseHubReport;
import coursehub.report.CourseHubReport.Anomaly;
import coursehub.report.CourseHubReport.AnomalyType;
import coursehub.report.CourseHubReport.CourseProgress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

public final class CourseHubAnalyzer {
    private static final int MAX_ANOMALY_SAMPLES = 10;

    public CourseHubReport analyze(List<Course> catalog, Stream<EnrollmentEvent> eventStream) {
        Objects.requireNonNull(catalog, "Каталог не должен быть null");
        Objects.requireNonNull(eventStream, "Поток событий не должен быть null");

        Map<Long, Course> coursesById = new HashMap<>();
        Map<CourseCode, MutableProgress> progressByCode = new TreeMap<>();
        EnumMap<CourseLevel, Long> courseCountByLevel = new EnumMap<>(CourseLevel.class);
        for (CourseLevel level : CourseLevel.values()) {
            courseCountByLevel.put(level, 0L);
        }

        for (Course course : catalog) {
            if (coursesById.putIfAbsent(course.id(), course) != null) {
                throw new IllegalArgumentException("В каталоге повторяется ID курса: " + course.id());
            }
            if (progressByCode.putIfAbsent(course.code(), new MutableProgress()) != null) {
                throw new IllegalArgumentException("В каталоге повторяется код курса: " + course.code());
            }
            courseCountByLevel.merge(course.level(), 1L, Long::sum);
        }

        Set<Long> uniqueStudents = new HashSet<>();
        Set<Registration> registrations = new HashSet<>();
        AnomalyAccumulator anomalies = new AnomalyAccumulator();

        try (Stream<EnrollmentEvent> events = eventStream.sequential()) {
            var iterator = events.iterator();
            while (iterator.hasNext()) {
                EnrollmentEvent event = Objects.requireNonNull(iterator.next(), "Событие не должно быть null");
                if (event.studentId() <= 0) {
                    anomalies.add(new Anomaly(AnomalyType.INVALID_STUDENT_ID, event.studentId(), event.courseId()));
                    continue;
                }
                uniqueStudents.add(event.studentId());

                Course course = coursesById.get(event.courseId());
                if (course == null) {
                    anomalies.add(new Anomaly(AnomalyType.UNKNOWN_COURSE, event.studentId(), event.courseId()));
                    continue;
                }

                Registration registration = new Registration(event.studentId(), event.courseId());
                if (!registrations.add(registration)) {
                    anomalies.add(new Anomaly(AnomalyType.DUPLICATE_ENROLLMENT, event.studentId(), event.courseId()));
                    continue;
                }

                progressByCode.get(course.code()).add(event.completed());
            }
        }

        Map<CourseCode, CourseProgress> reportProgress = new TreeMap<>();
        progressByCode.forEach((code, progress) -> reportProgress.put(code, progress.finish(code)));

        List<CourseProgress> topThree = new ArrayList<>(reportProgress.values());
        topThree.removeIf(progress -> progress.enrollmentCount() == 0);
        topThree.sort(Comparator.comparingLong(CourseProgress::enrollmentCount).reversed()
                .thenComparing(CourseProgress::courseCode));
        if (topThree.size() > 3) {
            topThree = new ArrayList<>(topThree.subList(0, 3));
        }

        return new CourseHubReport(
                uniqueStudents.size(),
                reportProgress,
                topThree,
                courseCountByLevel,
                anomalies.totalCount,
                anomalies.countsByType,
                List.copyOf(anomalies.samples)
        );
    }

    private static final class MutableProgress {
        private long enrollmentCount;
        private long completionCount;

        private void add(boolean completed) {
            enrollmentCount++;
            if (completed) {
                completionCount++;
            }
        }

        private CourseProgress finish(CourseCode code) {
            double rate = enrollmentCount == 0 ? 0.0 : completionCount * 100.0 / enrollmentCount;
            return new CourseProgress(code, enrollmentCount, completionCount, rate);
        }
    }

    private static final class AnomalyAccumulator {
        private final EnumMap<AnomalyType, Long> countsByType = new EnumMap<>(AnomalyType.class);
        private final TreeSet<Anomaly> samples = new TreeSet<>();
        private long totalCount;

        private AnomalyAccumulator() {
            for (AnomalyType type : AnomalyType.values()) {
                countsByType.put(type, 0L);
            }
        }

        private void add(Anomaly anomaly) {
            totalCount++;
            countsByType.merge(anomaly.type(), 1L, Long::sum);
            samples.add(anomaly);
            if (samples.size() > MAX_ANOMALY_SAMPLES) {
                samples.pollLast();
            }
        }
    }
}
