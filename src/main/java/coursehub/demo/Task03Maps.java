package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Task03Maps {

    public static void run() {
        System.out.println("\nЗадание 3. Map и индекс");

        List<Course> courses = List.of(
                new Course(101, "Java", 40),
                new Course(102, "SQL", 20)
        );

        Map<CourseId, Course> index = new LinkedHashMap<>();

        for (Course course : courses) {
            addCourse(index, course);
        }

        printIndex(index);

        Course found = index.get(new CourseId(101));

        System.out.println("\nget(101): " + found.title());
        System.out.println("get(999): " + index.get(new CourseId(999)));

        System.out.println(
                "containsKey(101): " + index.containsKey(new CourseId(101))
        );
        System.out.println(
                "containsKey(999): " + index.containsKey(new CourseId(999))
        );

        try {
            addCourse(index, new Course(101, "Другой курс", 30));
        } catch (IllegalArgumentException exception) {
            System.out.println("\nОшибка: " + exception.getMessage());
        }

        System.out.println(
                "Курс 101 после попытки замены: "
                        + index.get(new CourseId(101)).title()
        );

        Course created = index.computeIfAbsent(
                new CourseId(103),
                id -> {
                    System.out.println("\nСоздаём курс с ID " + id.value());
                    return new Course(id.value(), "Python", 50);
                }
        );

        System.out.println("Получили: " + created.title());

        Course existing = index.computeIfAbsent(
                new CourseId(103),
                id -> {
                    System.out.println("Эта строка не должна появиться");
                    return new Course(id.value(), "Замена Python", 60);
                }
        );

        System.out.println("Повторно получили: " + existing.title());

        printIndex(index);
    }

    private static void addCourse(
            Map<CourseId, Course> index,
            Course course
    ) {
        CourseId id = new CourseId(course.id());

        Course previous = index.putIfAbsent(id, course);

        if (previous != null) {
            throw new IllegalArgumentException(
                    "Курс с ID " + id.value() + " уже существует"
            );
        }
    }

    private static void printIndex(Map<CourseId, Course> index) {
        System.out.println("\nИндекс курсов:");

        for (Map.Entry<CourseId, Course> entry : index.entrySet()) {
            System.out.println(
                    entry.getKey().value() + " -> " + entry.getValue().title()
            );
        }
    }
}
