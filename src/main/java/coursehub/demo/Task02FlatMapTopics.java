package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseCode;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class Task02FlatMapTopics {
    private Task02FlatMapTopics() {
    }

    public static void run() {
        List<Course> courses = CourseExamples.catalog();
        Map<String, Set<CourseCode>> coursesByTopic = courses.stream()
                .flatMap(course -> course.modules().stream()
                        .map(topic -> new TopicCourse(normalize(topic), course.code())))
                .filter(item -> !item.topic().isBlank())
                .collect(Collectors.groupingBy(
                        TopicCourse::topic,
                        TreeMap::new,
                        Collectors.mapping(TopicCourse::courseCode, Collectors.toCollection(TreeSet::new))
                ));

        System.out.println("\nЗадание 2. flatMap и обратный индекс тем");
        System.out.println("Уникальные темы: " + coursesByTopic.keySet());
        coursesByTopic.forEach((topic, codes) -> System.out.println(topic + " -> " + codes));
    }

    private static String normalize(String topic) {
        return Normalizer.normalize(topic.strip(), Normalizer.Form.NFKC).toLowerCase(Locale.ROOT);
    }

    private record TopicCourse(String topic, CourseCode courseCode) {
    }
}
