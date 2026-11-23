package coursehub.enrollment;

public record ReviewRequired(String message) implements EnrollmentResult {
    @Override
    public Decision decision() {
        return Decision.REVIEW_REQUIRED;
    }
}
