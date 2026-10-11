package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseId;
import coursehub.model.Tag;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class Task11CollectionChoice {
    public static void run() {
        System.out.println("\nЗадание 11. Выбор по нагрузке");
        Course java = new Course(101, "Java", 40);
        Course sql = new Course(102, "SQL", 20);
        Course python = new Course(103, "Python", 50);

        Map<CourseId, Course> catalog = new HashMap<>();
        catalog.put(new CourseId(java.id()), java);
        System.out.println("Каталог по ID: " + catalog.get(new CourseId(101)).title());

        NavigableMap<Integer, List<String>> leaderboard = new TreeMap<>(Comparator.reverseOrder());
        leaderboard.computeIfAbsent(90, score -> new ArrayList<>()).add("Иван");
        leaderboard.computeIfAbsent(100, score -> new ArrayList<>()).add("Анна");
        leaderboard.computeIfAbsent(90, score -> new ArrayList<>()).add("Пётр");
        System.out.println("Leaderboard, включая равные баллы: " + leaderboard);

        Queue<String> queue = new ArrayDeque<>();
        queue.offer("Иван");
        queue.offer("Анна");
        System.out.println("Следующий в очереди: " + queue.poll());

        Set<Tag> tags = new LinkedHashSet<>(List.of(new Tag("java"), new Tag("sql"), new Tag("java")));
        System.out.println("Уникальные теги по порядку: " + tags);

        NavigableMap<CourseId, Course> ranges = new TreeMap<>();
        ranges.put(new CourseId(java.id()), java);
        ranges.put(new CourseId(sql.id()), sql);
        ranges.put(new CourseId(python.id()), python);
        System.out.println("Диапазон ID 101–102: "
                + ranges.subMap(new CourseId(101), true, new CourseId(102), true).keySet());

        List<Course> byIndex = new ArrayList<>(List.of(java, sql, python));
        System.out.println("Курс по индексу 1: " + byIndex.get(1).title());
        System.out.println("Сложности и цена порядка/памяти: docs/explanations.md, №11.");
    }
}
