package coursehub.demo;

import coursehub.format.CourseFormatter;
import coursehub.model.Course;

import java.time.Clock;
import java.time.Instant;

public final class Task05TypeChoices {
    private Task05TypeChoices() {
    }

    public static void run() {
        System.out.println("\nЗадание 5. Интерфейс или абстрактный класс");
        CourseEntity entity = new CourseEntity(303);
        CoursePolicy policy = course -> course.durationHours() <= 60;
        Course course = new Course(entity.id(), "Design Patterns", 48);
        System.out.println("Identifiable — interface: общий контракт для разных сущностей; цена — реализовать id в каждом типе.");
        System.out.println("CoursePolicy — interface: политики подменяются независимо; цена — общий контракт при изменении.");
        System.out.println("BaseEntity — abstract class: хранит общий id; цена — занятое одиночное наследование.");
        System.out.println("Formatter — interface: CourseFormatter поддерживает несколько форматов; цена — реализация метода в каждом формате.");
        System.out.println("ClockProvider — отдельный тип не нужен: java.time.Clock уже позволяет передать фиксированное время.");
        System.out.println("policy allows=" + policy.allows(course) + ", entity=" + entity.id()
                + ", time=" + Clock.fixed(Instant.parse("2026-11-23T00:00:00Z"), java.time.ZoneOffset.UTC).instant());
    }

    private interface Identifiable {
        long id();
    }

    private interface CoursePolicy {
        boolean allows(Course course);
    }

    private abstract static class BaseEntity implements Identifiable {
        private final long id;

        private BaseEntity(long id) {
            this.id = id;
        }

        @Override
        public final long id() {
            return id;
        }
    }

    private static final class CourseEntity extends BaseEntity {
        private CourseEntity(long id) {
            super(id);
        }
    }
}
