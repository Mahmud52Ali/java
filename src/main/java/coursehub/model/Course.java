package coursehub.model;

import java.util.List;
import java.util.Objects;

public record Course(
        long id,
        CourseCode code,
        String title,
        CourseLevel level,
        CourseDuration duration,
        List<String> modules
) {
    public Course {
        if (id <= 0) {
            throw new IllegalArgumentException("ID курса должен быть положительным");
        }
        Objects.requireNonNull(code, "Код курса не должен быть null");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название курса не должно быть пустым");
        }
        Objects.requireNonNull(level, "Уровень курса не должен быть null");
        Objects.requireNonNull(duration, "Длительность не должна быть null");
        if (duration.hours() < 1) {
            throw new IllegalArgumentException("Длительность курса должна быть положительной");
        }
        title = title.strip();
        modules = List.copyOf(Objects.requireNonNull(modules, "Список модулей не должен быть null"));
    }
}
