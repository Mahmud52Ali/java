package coursehub.enrollment;

public final class ResultDescriptions {
    private ResultDescriptions() {
    }

    public static String describe(EnrollmentResult result) {
        return switch (result) {
            case Accepted accepted -> "Accepted: " + accepted.message();
            case Rejected rejected -> "Rejected: " + rejected.message();
            case WaitListed waitListed -> "Wait-listed: " + waitListed.message();
            case ReviewRequired reviewRequired -> "Review required: " + reviewRequired.message();
        };
    }
}
