package coursehub.enrollment;

import coursehub.model.Course;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class AtomicCourseEnrollment implements CourseEnrollment {
	private final Course course;
	private final int capacity;
	private final AtomicReference<Set<Long>> students = new AtomicReference<>(Set.of());

	public AtomicCourseEnrollment(Course course, int capacity) {
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
		while (true) {
			Set<Long> current = students.get();
			if (current.contains(studentId) || current.size() >= capacity) {
				return false;
			}
			Set<Long> next = new HashSet<>(current);
			next.add(studentId);
			if (students.compareAndSet(current, Set.copyOf(next))) {
				return true;
			}
			// Конкурент изменил снимок: заново проверяем оба условия.
		}
	}

	@Override
	public Set<Long> students() {
		return students.get();
	}

	@Override
	public int capacity() {
		return capacity;
	}

	public Course course() {
		return course;
	}
}
