package coursehub.model;

public class Course {
    private final long id;
    private final String title;
    private final int durationHours;
    private final int completedHours;

    public Course(long id, String title, int durationHours, int completedHours) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID курса должен быть положительным");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название курса не должно быть пустым");
        }
        if (durationHours < 1 || durationHours > 300) {
            throw new IllegalArgumentException("Длительность должна быть от 1 до 300 часов");
        }
        if (completedHours < 0 || completedHours > durationHours) {
            throw new IllegalArgumentException("Пройденные часы должны быть от 0 до длительности");
        }
        this.id = id;
        this.title = title.strip();
        this.durationHours = durationHours;
        this.completedHours = completedHours;
    }

    public Course(long id, String title, int durationHours) {
        this(id, title, durationHours, 0);
    }

    public long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public int durationHours() {
        return durationHours;
    }

    public int completedHours() {
        return completedHours;
    }

    @Override
    public String toString() {
        return "Course[id=" + id + ", title=" + title + ", durationHours="
                + durationHours + ", completedHours=" + completedHours + "]";
    }
}
