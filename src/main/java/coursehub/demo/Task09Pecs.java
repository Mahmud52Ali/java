package coursehub.demo;

import coursehub.generics.ListCopier;
import coursehub.model.Course;

import java.util.ArrayList;
import java.util.List;

public class Task09Pecs {

    public static void run() {
        System.out.println("\nЗадание 9. PECS");

        List<OnlineCourse> onlineCourses = List.of(
                new OnlineCourse(101, "Java", 40),
                new OnlineCourse(102, "SQL", 20)
        );

        List<Course> courses = new ArrayList<>();

        ListCopier.copy(onlineCourses, courses);

        System.out.println("Курсы после копирования:");

        for (Course course : courses) {
            System.out.println(course.id() + " -> " + course.title());
        }

        List<Object> objects = new ArrayList<>();

        ListCopier.copy(courses, objects);

        System.out.println("\nКоличество объектов: " + objects.size());

        List<? extends Course> producer = onlineCourses;

        Course first = producer.get(0);

        System.out.println("Прочитали из producer: " + first.title());

        List<? super Course> consumer = objects;

        consumer.add(new Course(103, "Python", 30));
        consumer.add(new OnlineCourse(104, "Git", 10));

        Object element = consumer.get(0);

        System.out.println("Прочитали из consumer: " + element.getClass().getSimpleName());
        System.out.println("Размер после добавления: " + objects.size());

        /* Эти операции не компилируются из-за неизвестного точного типа:
           producer.add(new Course(103, "Python", 30));
           Course course = consumer.get(0);
        */
    }

    private static class OnlineCourse extends Course {

        public OnlineCourse(long id, String title, int durationHours) {
            super(id, title, durationHours);
        }
    }
}
