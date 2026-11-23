package coursehub.enrollment;

public record Accepted(String message) implements EnrollmentResult {
    @Override
    public Decision decision() {
        return Decision.ACCEPTED;
    }
}
