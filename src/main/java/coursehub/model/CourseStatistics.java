package coursehub.model;

import java.util.OptionalLong;

public record CourseStatistics(long count, long sumHours, OptionalLong minHours, OptionalLong maxHours) {
    public CourseStatistics {
        if (count < 0 || sumHours < 0 || minHours == null || maxHours == null) {
            throw new IllegalArgumentException("Некорректная статистика курсов");
        }
        if (count == 0 && (minHours.isPresent() || maxHours.isPresent())) {
            throw new IllegalArgumentException("Для пустой статистики минимум и максимум должны отсутствовать");
        }
        if (count > 0 && (minHours.isEmpty() || maxHours.isEmpty())) {
            throw new IllegalArgumentException("Для непустой статистики нужны минимум и максимум");
        }
    }
}
