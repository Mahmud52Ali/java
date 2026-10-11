package coursehub.demo;

import coursehub.model.Registration;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

public class Task05Removal {

    public static void run() {
        System.out.println("\nЗадание 5. Удаление при обходе");

        demonstrateIncorrectRemoval();
        demonstrateIteratorRemoval();
        demonstrateRemoveIf();
    }

    private static List<Registration> createRegistrations() {
        return new ArrayList<>(List.of(
                new Registration(1, 101),
                new Registration(2, 102),
                new Registration(3, 101),
                new Registration(4, 103)
        ));
    }

    private static void demonstrateIncorrectRemoval() {
        List<Registration> registrations = createRegistrations();

        System.out.println("\nНеправильное удаление:");
        System.out.println("До: " + registrations);

        try {
            for (Registration registration : registrations) {
                if (registration.courseId() == 101) {
                    registrations.remove(registration);
                }
            }
        } catch (ConcurrentModificationException exception) {
            System.out.println(
                    "Получили ConcurrentModificationException"
            );
        }

        System.out.println("После ошибки: " + registrations);
    }

    private static void demonstrateIteratorRemoval() {
        List<Registration> registrations = createRegistrations();

        System.out.println("\nУдаление через Iterator:");
        System.out.println("До: " + registrations);

        Iterator<Registration> iterator = registrations.iterator();

        while (iterator.hasNext()) {
            Registration registration = iterator.next();

            if (registration.courseId() == 101) {
                iterator.remove();
            }
        }

        System.out.println("После: " + registrations);
    }

    private static void demonstrateRemoveIf() {
        List<Registration> registrations = createRegistrations();

        System.out.println("\nУдаление через removeIf:");
        System.out.println("До: " + registrations);

        boolean changed = registrations.removeIf(
                registration -> registration.courseId() == 101
        );

        System.out.println("Список изменился: " + changed);
        System.out.println("После: " + registrations);
    }
}