package coursehub.format;

import coursehub.model.Course;

public class DetailedCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return "Course #" + course.id() + ": " + course.title() + ", " + course.durationHours() + " hours";
    }
}
