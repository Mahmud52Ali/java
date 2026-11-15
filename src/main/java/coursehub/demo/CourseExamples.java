package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseCode;
import coursehub.model.CourseDuration;
import coursehub.model.CourseLevel;
import java.util.List;

public final class CourseExamples {
	private CourseExamples() {
	}

	public static Course javaCourse() {
		return new Course(101, new CourseCode("JAVA-101"), "Основы Java",
				CourseLevel.BEGINNER, new CourseDuration(40), List.of("Многопоточность"));
	}
}
