package coursehub.report;

import coursehub.model.CourseCode;
import coursehub.model.CourseLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public record CourseHubReport(
        long uniqueStudentCount,
        Map<CourseCode, CourseProgress> progressByCourse,
        List<CourseProgress> topThreeByEnrollment,
        Map<CourseLevel, Long> courseCountByLevel,
        long anomalyCount,
        Map<AnomalyType, Long> anomaliesByType,
        List<Anomaly> anomalySamples
) {
    public CourseHubReport {
        progressByCourse = Collections.unmodifiableMap(new TreeMap<>(progressByCourse));
        topThreeByEnrollment = List.copyOf(topThreeByEnrollment);

        EnumMap<CourseLevel, Long> levelCopy = new EnumMap<>(CourseLevel.class);
        levelCopy.putAll(courseCountByLevel);
        for (CourseLevel level : CourseLevel.values()) {
            levelCopy.putIfAbsent(level, 0L);
        }
        courseCountByLevel = Collections.unmodifiableMap(levelCopy);

        EnumMap<AnomalyType, Long> anomalyCopy = new EnumMap<>(AnomalyType.class);
        anomalyCopy.putAll(anomaliesByType);
        for (AnomalyType type : AnomalyType.values()) {
            anomalyCopy.putIfAbsent(type, 0L);
        }
        anomaliesByType = Collections.unmodifiableMap(anomalyCopy);

        ArrayList<Anomaly> sortedSamples = new ArrayList<>(anomalySamples);
        Collections.sort(sortedSamples);
        anomalySamples = List.copyOf(sortedSamples);
    }

    public record CourseProgress(
            CourseCode courseCode,
            long enrollmentCount,
            long completionCount,
            double completionRatePercent
    ) {
    }

    public enum AnomalyType {
        INVALID_STUDENT_ID,
        UNKNOWN_COURSE,
        DUPLICATE_ENROLLMENT
    }

    public record Anomaly(AnomalyType type, long studentId, long courseId) implements Comparable<Anomaly> {
        @Override
        public int compareTo(Anomaly other) {
            int byType = type.compareTo(other.type);
            if (byType != 0) {
                return byType;
            }
            int byStudent = Long.compare(studentId, other.studentId);
            if (byStudent != 0) {
                return byStudent;
            }
            return Long.compare(courseId, other.courseId);
        }
    }
}
