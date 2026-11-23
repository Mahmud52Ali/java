package coursehub.enrollment;

public record Rejected(String message) implements EnrollmentResult {
    @Override
    public Decision decision() {
        return Decision.REJECTED;
    }
}
