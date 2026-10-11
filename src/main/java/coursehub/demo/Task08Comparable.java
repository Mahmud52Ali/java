package coursehub.demo;

import coursehub.generics.GenericAlgorithms;
import coursehub.model.CourseId;

import java.util.List;
import java.util.NoSuchElementException;

public class Task08Comparable {

    public static void run() {
        System.out.println("\nЗадание 8. Ограничение типа");

        List<Integer> numbers = List.of(7, 3, 12, 5);

        Integer maximumNumber = GenericAlgorithms.max(numbers);

        System.out.println("Максимальное число: " + maximumNumber);

        List<String> names = List.of("Java", "SQL", "Python");

        String maximumName = GenericAlgorithms.max(names);

        System.out.println("Максимальная строка: " + maximumName);

        List<CourseId> ids = List.of(
                new CourseId(101),
                new CourseId(305),
                new CourseId(204)
        );

        CourseId maximumId = GenericAlgorithms.max(ids);

        System.out.println("Максимальный ID: " + maximumId.value());

        try {
            GenericAlgorithms.max(List.<Integer>of());
        } catch (NoSuchElementException exception) {
            System.out.println("Ошибка: " + exception.getMessage());
        }
    }
}