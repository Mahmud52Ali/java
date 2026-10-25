package coursehub.storage;

import coursehub.model.Course;
import java.util.List;
import java.util.stream.Collectors;

public final class CourseTextFormat {
    private CourseTextFormat() {
    }

    public static String formatCatalog(List<Course> courses) {
        if (courses.isEmpty()) {
            return "";
        }
        return courses.stream()
                .map(CourseTextFormat::formatCourse)
                .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }

    public static String formatCourse(Course course) {
        if (course.title().contains(";") || course.title().contains("\n") || course.title().contains("\r")) {
            throw new IllegalArgumentException("Название не должно содержать ; или перевод строки");
        }
        return course.id() + ";" + course.title() + ";" + course.durationHours()
                + ";" + course.completedHours();
    }

    public static Course parseCourse(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("ожидались поля id;title;durationHours;completedHours");
        }
        try {
            return new Course(
                    Long.parseLong(fields[0].strip()),
                    fields[1],
                    Integer.parseInt(fields[2].strip()),
                    Integer.parseInt(fields[3].strip()));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("ID и часы должны быть целыми числами", exception);
        }
    }
}
