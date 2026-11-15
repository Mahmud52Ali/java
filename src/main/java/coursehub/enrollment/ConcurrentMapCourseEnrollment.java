package coursehub.enrollment;

import coursehub.model.Course;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ConcurrentMapCourseEnrollment implements CourseEnrollment {
	private final Course course;
	private final int capacity;
	private final ConcurrentHashMap<Long, Set<Long>> studentsByCourse = new ConcurrentHashMap<>();

	public ConcurrentMapCourseEnrollment(Course course, int capacity) {
		this.course = Objects.requireNonNull(course);
		if (capacity < 0) {
			throw new IllegalArgumentException("Вместимость не может быть отрицательной");
		}
		this.capacity = capacity;
		studentsByCourse.put(course.id(), Set.of());
	}

	@Override
	public boolean enroll(long studentId) {
		if (studentId <= 0) {
			throw new IllegalArgumentException("Нужен положительный ID студента");
		}
		AtomicBoolean enrolled = new AtomicBoolean();
		studentsByCourse.compute(course.id(), (id, current) -> {
			if (current.contains(studentId) || current.size() >= capacity) {
				return current;
			}
			Set<Long> next = new HashSet<>(current);
			next.add(studentId);
			enrolled.set(true);
			return Set.copyOf(next);
		});
		return enrolled.get();
	}

	@Override
	public Set<Long> students() {
		return studentsByCourse.get(course.id());
	}

	@Override
	public int capacity() {
		return capacity;
	}

	public Course course() {
		return course;
	}
}
