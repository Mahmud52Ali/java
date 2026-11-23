package coursehub.demo;

public final class Task10DefaultConflict {
    private Task10DefaultConflict() {
    }

    public static void run() {
        System.out.println("\nЗадание 10. Конфликт default-методов");
        System.out.println(new CourseDescription().label());
        System.out.println("Класс обязан явно выбрать поведение двух интерфейсов.");
    }

    private interface ShortLabel {
        default String label() {
            return "short label";
        }
    }

    private interface LongLabel {
        default String label() {
            return "detailed label";
        }
    }

    private static final class CourseDescription implements ShortLabel, LongLabel {
        @Override
        public String label() {
            return ShortLabel.super.label() + " + " + LongLabel.super.label();
        }
    }
}
