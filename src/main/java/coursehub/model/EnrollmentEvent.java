package coursehub.model;

public record EnrollmentEvent(long studentId, long courseId, boolean completed) {
}
