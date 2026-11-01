package coursehub.model;

import java.util.Objects;

public record CourseDuration(long hours) implements Comparable<CourseDuration> {
    public static final CourseDuration ZERO = new CourseDuration(0);

    public CourseDuration {
        if (hours < 0) {
            throw new IllegalArgumentException("Длительность не может быть отрицательной");
        }
    }

    public CourseDuration plus(CourseDuration other) {
        Objects.requireNonNull(other, "Другая длительность не должна быть null");
        return new CourseDuration(Math.addExact(hours, other.hours));
    }

    @Override
    public int compareTo(CourseDuration other) {
        return Long.compare(hours, other.hours);
    }

    @Override
    public String toString() {
        return hours + " ч";
    }
}
