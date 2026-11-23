package coursehub.enrollment;

public sealed interface EnrollmentResult permits Accepted, Rejected, WaitListed, ReviewRequired {
    enum Decision {
        ACCEPTED,
        REJECTED,
        WAIT_LISTED,
        REVIEW_REQUIRED
    }

    Decision decision();

    String message();
}
