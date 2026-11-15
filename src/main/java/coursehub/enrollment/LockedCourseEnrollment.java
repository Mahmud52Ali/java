package coursehub.enrollment;

import coursehub.model.Course;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class LockedCourseEnrollment implements CourseEnrollment {
	private final Course course;
	private final int capacity;
	private final Object monitor = new Object();
	private final Set<Long> students = new HashSet<>();

	public LockedCourseEnrollment(Course course, int capacity) {
		this.course = Objects.requireNonNull(course);
		if (capacity < 0) {
			throw new IllegalArgumentException("Вместимость не может быть отрицательной");
		}
		this.capacity = capacity;
	}

	@Override
	public boolean enroll(long studentId) {
		if (studentId <= 0) {
			throw new IllegalArgumentException("Нужен положительный ID студента");
		}
		synchronized (monitor) {
			if (students.contains(studentId) || students.size() >= capacity) {
				return false;
			}
			return students.add(studentId);
		}
	}

	@Override
	public Set<Long> students() {
		synchronized (monitor) {
			return Set.copyOf(students);
		}
	}

	@Override
	public int capacity() {
		return capacity;
	}

	public Course course() {
		return course;
	}
}
