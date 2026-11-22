package coursehub.async.model;

public record Course(long id, String code, String title, int durationHours) {
    public Course {
        if (id <= 0 || durationHours <= 0) {
            throw new IllegalArgumentException("ID и длительность курса должны быть положительными");
        }
        if (code == null || code.isBlank() || title == null || title.isBlank()) {
            throw new IllegalArgumentException("Код и название курса не должны быть пустыми");
        }
    }
}
