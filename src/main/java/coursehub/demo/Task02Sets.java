package coursehub.demo;

import coursehub.model.CourseCode;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Task02Sets {

    public static void run() {
        System.out.println("\nЗадание 2. Set и уникальность");

        List<CourseCode> codes = List.of(
                new CourseCode("JAVA"),
                new CourseCode("SQL"),
                new CourseCode("JAVA"),
                new CourseCode("PYTHON"),
                new CourseCode("SQL")
        );

        System.out.println("Исходные коды:");
        System.out.println(codes);

        Set<CourseCode> uniqueCodes = new LinkedHashSet<>(codes);

        System.out.println("\nУникальные коды в порядке первого появления:");
        System.out.println(uniqueCodes);
    }
}