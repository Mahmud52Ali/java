package coursehub.format;

import coursehub.model.Course;

public class CompactCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return course.id() + " — " + course.title();
    }
}
