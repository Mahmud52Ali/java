package coursehub.demo;

import coursehub.concurrent.ConcurrentBatch;
import coursehub.enrollment.AtomicCourseEnrollment;
import coursehub.enrollment.ConcurrentMapCourseEnrollment;
import coursehub.enrollment.CourseEnrollment;
import coursehub.enrollment.LockedCourseEnrollment;
import java.util.List;

public final class Task06CourseEnrollment {
	private Task06CourseEnrollment() {
	}

	public static void run() throws Exception {
		System.out.println("\nЗадание 6. Запись на курс с ограничением мест");
		var course = CourseExamples.javaCourse();
		for (CourseEnrollment enrollment : List.of(new LockedCourseEnrollment(course, 30),
				new AtomicCourseEnrollment(course, 30), new ConcurrentMapCourseEnrollment(course, 30))) {
			var results = ConcurrentBatch.run(8, 400, index -> enrollment.enroll(index % 100 + 1));
			long successes = results.stream().filter(Boolean::booleanValue).count();
			var before = enrollment.students();
			boolean rejected = !enrollment.enroll(999);
			System.out.println(enrollment.getClass().getSimpleName() + ": вызовов=400, успешных=" + successes
					+ ", студентов=" + enrollment.students().size() + ", capacity=" + enrollment.capacity()
					+ ", отказ без изменений=" + (rejected && before.equals(enrollment.students())));
		}
	}
}
