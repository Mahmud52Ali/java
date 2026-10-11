package coursehub.demo;

import coursehub.model.Registration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Task01Lists {

    public static void run() {
        System.out.println("Задание 1. List и порядок");

        demonstrateOperations();
        compareLists();
    }

    private static void demonstrateOperations() {
        List<Registration> registrations = new ArrayList<>();

        registrations.add(new Registration(1, 101));
        registrations.add(new Registration(2, 101));
        registrations.add(new Registration(1, 101));
        registrations.add(new Registration(3, 102));

        System.out.println("\nИсходный список:");
        System.out.println(registrations);

        registrations.add(1, new Registration(4, 103));

        System.out.println("\nПосле вставки по индексу 1:");
        System.out.println(registrations);

        registrations.remove(new Registration(2, 101));

        System.out.println("\nПосле удаления студента 2 с курса 101:");
        System.out.println(registrations);

        List<Registration> seen = new ArrayList<>();
        List<Registration> duplicates = new ArrayList<>();

        for (Registration registration : registrations) {
            if (seen.contains(registration)) {
                if (!duplicates.contains(registration)) {
                    duplicates.add(registration);
                }
            } else {
                seen.add(registration);
            }
        }

        System.out.println("\nДубликаты:");
        System.out.println(duplicates);
    }

    private static void compareLists() {
        int size = 10_000;

        List<Registration> arrayList = new ArrayList<>();
        List<Registration> linkedList = new LinkedList<>();

        for (int i = 1; i <= size; i++) {
            Registration registration = new Registration(i, 101);

            arrayList.add(registration);
            linkedList.add(registration);
        }

        measure(arrayList, true);
        measure(linkedList, true);
        measure(arrayList, false);
        measure(linkedList, false);

        System.out.println("\nСравнение на " + size + " элементах:");

        printMeasurement(
                "ArrayList, чтение по индексу",
                measure(arrayList, true)
        );

        printMeasurement(
                "LinkedList, чтение по индексу",
                measure(linkedList, true)
        );

        printMeasurement(
                "ArrayList, for-each",
                measure(arrayList, false)
        );

        printMeasurement(
                "LinkedList, for-each",
                measure(linkedList, false)
        );

        System.out.println(
                "\nДля списка регистраций выбираем ArrayList: "
                        + "он сохраняет порядок и обеспечивает "
                        + "быстрый доступ по индексу."
        );

        System.out.println(
                "Это учебное измерение, не микробенчмарк. "
                        + "Время зависит от JVM и нагрузки компьютера."
        );
    }

    private static Measurement measure(
            List<Registration> registrations,
            boolean byIndex
    ) {
        long checksum = 0;
        long start = System.nanoTime();

        if (byIndex) {
            for (int i = 0; i < registrations.size(); i++) {
                checksum += registrations.get(i).studentId();
            }
        } else {
            for (Registration registration : registrations) {
                checksum += registration.studentId();
            }
        }

        long elapsed = System.nanoTime() - start;

        return new Measurement(elapsed, checksum);
    }

    private static void printMeasurement(
            String label,
            Measurement measurement
    ) {
        System.out.printf(
                "%s: %.3f мс, сумма ID: %d%n",
                label,
                measurement.nanoseconds() / 1_000_000.0,
                measurement.checksum()
        );
    }

    private record Measurement(long nanoseconds, long checksum) {
    }
}
