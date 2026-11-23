package coursehub.enrollment;

public record WaitListed(String message) implements EnrollmentResult {
    @Override
    public Decision decision() {
        return Decision.WAIT_LISTED;
    }
}
