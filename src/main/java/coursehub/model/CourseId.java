package coursehub.model;

public record CourseId(long value) implements Comparable<CourseId> {

    @Override
    public int compareTo(CourseId other) {
        return Long.compare(value, other.value);
    }
}