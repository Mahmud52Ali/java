package coursehub.demo;

import coursehub.cache.LruCache;
import coursehub.model.Course;
import coursehub.model.CourseId;

public class Task14LruCache {
    public static void run() {
        System.out.println("\nЗадание 14. LRU-кэш");
        LruCache<String, String> cache = new LruCache<>(2);
        cache.put("A", "Первый");
        cache.put("B", "Второй");
        System.out.println("От старого к недавнему: " + cache.keysInLruOrder());
        cache.get("A");
        System.out.println("После get(A): " + cache.keysInLruOrder());
        cache.put("C", "Третий");
        System.out.println("После put(C): " + cache.keysInLruOrder());
        System.out.println("B вытеснен: " + cache.get("B").isEmpty());

        cache.put("A", "Первый обновлённый");
        System.out.println("Обновление A меняет недавность: " + cache.keysInLruOrder());
        cache.put("D", "Четвёртый");
        System.out.println("После put(D): " + cache.keysInLruOrder());
        System.out.println("C вытеснен: " + cache.get("C").isEmpty());

        LruCache<CourseId, Course> courses = new LruCache<>(1);
        Course course = new Course(101, "Java", 40);
        courses.put(new CourseId(course.id()), course);
        course.completeHours(6);
        System.out.println("Неизменяемый ID остаётся доступен: "
                + courses.get(new CourseId(101)).orElseThrow().completedHours());
        try {
            cache.put("E", null);
        } catch (NullPointerException exception) {
            System.out.println("Null запрещён: " + exception.getMessage());
        }
    }
}
