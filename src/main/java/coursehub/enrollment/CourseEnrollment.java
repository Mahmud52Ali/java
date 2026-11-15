package coursehub.enrollment;

import java.util.Set;

public interface CourseEnrollment {
	boolean enroll(long studentId);
	Set<Long> students();
	int capacity();
}
