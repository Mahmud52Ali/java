package coursehub.demo;

import coursehub.generics.GenericAlgorithms;
import coursehub.model.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Task07GenericMethods {

    public static void run() {
        System.out.println("\nЗадание 7. Generic-методы");

        List<String> names = new ArrayList<>(
                List.of("Иван", "Анна", "Пётр")
        );

        String firstName = GenericAlgorithms.firstOrThrow(names);
        System.out.println("Первое имя: " + firstName);

        List<Integer> numbers = List.of(10, 20, 30);

        Integer firstNumber = GenericAlgorithms.firstOrThrow(numbers);
        System.out.println("Первое число: " + firstNumber);

        try {
            GenericAlgorithms.firstOrThrow(List.<String>of());
        } catch (NoSuchElementException exception) {
            System.out.println("Ошибка: " + exception.getMessage());
        }

        System.out.println("\nДо перестановки: " + names);

        GenericAlgorithms.swap(names, 0, 2);

        System.out.println("После перестановки: " + names);

        List<Registration> registrations = List.of(
                new Registration(1, 101),
                new Registration(2, 102),
                new Registration(3, 101)
        );

        Map<Long, Registration> index = GenericAlgorithms.indexBy(
                registrations,
                registration -> registration.studentId()
        );

        System.out.println("\nИндекс по ID студента:");
        System.out.println(index);

        List<Registration> duplicates = List.of(
                new Registration(1, 101),
                new Registration(1, 102)
        );

        try {
            GenericAlgorithms.indexBy(
                    duplicates,
                    registration -> registration.studentId()
            );
        } catch (IllegalArgumentException exception) {
            System.out.println("\nОшибка: " + exception.getMessage());
        }
    }
}
