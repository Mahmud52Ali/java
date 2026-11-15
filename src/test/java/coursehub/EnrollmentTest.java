package coursehub;

import coursehub.concurrent.ConcurrentBatch;
import coursehub.demo.CourseExamples;
import coursehub.enrollment.AtomicCourseEnrollment;
import coursehub.enrollment.ConcurrentMapCourseEnrollment;
import coursehub.enrollment.CourseEnrollment;
import coursehub.enrollment.LockedCourseEnrollment;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(10)
class EnrollmentTest {
	static Stream<IntFunction<CourseEnrollment>> variants() {
		return Stream.of(capacity -> new LockedCourseEnrollment(CourseExamples.javaCourse(), capacity),
				capacity -> new AtomicCourseEnrollment(CourseExamples.javaCourse(), capacity),
				capacity -> new ConcurrentMapCourseEnrollment(CourseExamples.javaCourse(), capacity));
	}

	@ParameterizedTest
	@MethodSource("variants")
	void capacityAndUniquenessHoldUnderContention(IntFunction<CourseEnrollment> factory) throws Exception {
		CourseEnrollment enrollment = factory.apply(30);
		var results = ConcurrentBatch.run(16, 800, index -> enrollment.enroll(index % 100 + 1));
		assertEquals(30, results.stream().filter(Boolean::booleanValue).count());
		assertEquals(30, enrollment.students().size());
		assertTrue(enrollment.students().stream().allMatch(id -> id >= 1 && id <= 100));
		Set<Long> before = enrollment.students();
		assertFalse(enrollment.enroll(999));
		assertEquals(before, enrollment.students());
	}

	@ParameterizedTest
	@MethodSource("variants")
	void oneStudentTakesExactlyOnePlace(IntFunction<CourseEnrollment> factory) throws Exception {
		CourseEnrollment enrollment = factory.apply(30);
		var results = ConcurrentBatch.run(16, 300, index -> enrollment.enroll(42));
		assertEquals(1, results.stream().filter(Boolean::booleanValue).count());
		assertEquals(Set.of(42L), enrollment.students());
		assertTrue(enrollment.enroll(43));
	}

	@ParameterizedTest
	@MethodSource("variants")
	void snapshotsCannotModifyEnrollment(IntFunction<CourseEnrollment> factory) {
		CourseEnrollment enrollment = factory.apply(2);
		assertTrue(enrollment.enroll(1));
		Set<Long> snapshot = enrollment.students();
		assertThrows(UnsupportedOperationException.class, () -> snapshot.add(2L));
		assertTrue(enrollment.enroll(2));
		assertEquals(Set.of(1L), snapshot);
		assertFalse(enrollment.enroll(2));
		assertEquals(Set.of(1L, 2L), enrollment.students());
	}

	@ParameterizedTest
	@MethodSource("variants")
	void rejectsInvalidInputWithoutChangingState(IntFunction<CourseEnrollment> factory) {
		CourseEnrollment enrollment = factory.apply(2);
		assertTrue(enrollment.enroll(1));
		var before = enrollment.students();
		assertThrows(IllegalArgumentException.class, () -> enrollment.enroll(0));
		assertThrows(IllegalArgumentException.class, () -> enrollment.enroll(-1));
		assertEquals(before, enrollment.students());
		assertThrows(IllegalArgumentException.class, () -> factory.apply(-1));
	}

	@ParameterizedTest
	@MethodSource("variants")
	void zeroCapacityNeverAcceptsStudent(IntFunction<CourseEnrollment> factory) throws Exception {
		CourseEnrollment enrollment = factory.apply(0);
		var results = ConcurrentBatch.run(8, 50, index -> enrollment.enroll(index + 1));
		assertTrue(results.stream().noneMatch(Boolean::booleanValue));
		assertTrue(enrollment.students().isEmpty());
	}
}
