package coursehub.model;

import java.util.Locale;

public record CourseCode(String value) implements Comparable<CourseCode> {
    public CourseCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Код курса не должен быть пустым");
        }
        value = value.strip().toUpperCase(Locale.ROOT);
    }

    @Override
    public int compareTo(CourseCode other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
