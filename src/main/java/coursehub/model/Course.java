package coursehub.model;

public record Course(long id, String title, int durationHours) {
    public Course {
        if (id <= 0 || title == null || title.isBlank() || durationHours <= 0) {
            throw new IllegalArgumentException("Invalid course data");
        }
    }
}
