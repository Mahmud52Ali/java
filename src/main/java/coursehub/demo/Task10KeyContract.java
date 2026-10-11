package coursehub.demo;

import coursehub.model.Course;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* В обычном Course равенство и хеш зависят только от ID.
   BrokenHashCourse намеренно включает изменяемый прогресс в хеш. */
public class Task10KeyContract {
    public static void run() {
        System.out.println("\nЗадание 10. Контракт ключа");
        Course course = new Course(101, "Java", 40);
        Set<Course> courses = new HashSet<>();
        Map<Course, String> notes = new HashMap<>();
        courses.add(course);
        notes.put(course, "Обычный ключ");

        int oldHash = course.hashCode();
        course.completeHours(6);
        Course sameId = new Course(101, "Другое название", 20);
        System.out.println("Прогресс: " + course.completedHours());
        System.out.println("Хеш остался прежним: " + (oldHash == course.hashCode()));
        System.out.println("HashSet находит равный курс: " + courses.contains(sameId));
        System.out.println("HashMap находит равный ключ: " + notes.get(sameId));

        Course broken = new BrokenHashCourse(102, "SQL", 30);
        Set<Course> brokenSet = new HashSet<>();
        Map<Course, String> brokenMap = new HashMap<>();
        brokenSet.add(broken);
        brokenMap.put(broken, "Нестабильный ключ");
        System.out.println("\nДо изменения: " + brokenSet.contains(broken));
        int brokenHash = broken.hashCode();
        broken.completeHours(6);
        System.out.println("Хеш изменился: " + (brokenHash != broken.hashCode()));
        System.out.println("После изменения HashSet.contains: " + brokenSet.contains(broken));
        System.out.println("После изменения HashMap.get: " + brokenMap.get(broken));
        System.out.println("Элементы остались внутри: " + brokenSet.size() + ", " + brokenMap.size());
    }

    private static class BrokenHashCourse extends Course {
        BrokenHashCourse(long id, String title, int durationHours) {
            super(id, title, durationHours);
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + completedHours();
        }
    }
}
