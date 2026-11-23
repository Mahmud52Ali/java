package coursehub.demo;

import coursehub.model.Course;

public final class Task06OverrideMistakes {
    private Task06OverrideMistakes() {
    }

    public static void run() {
        System.out.println("\nЗадание 6. Переопределение без случайной перегрузки");
        BaseFormatter formatter = new SpecialFormatter();
        SpecialCourse special = new SpecialCourse(new Course(404, "Cloud", 24));
        System.out.println("Через BaseFormatter: " + formatter.format(special.course()));
        System.out.println("Через SpecialFormatter: " + ((SpecialFormatter) formatter).format(special));
        System.out.println("Сигнатуры параметров должны совпадать; доступ нельзя сузить; возвращаемый тип может быть ковариантным.");
        System.out.println("Без @Override format(SpecialCourse) компилируется как перегрузка, но ссылка BaseFormatter вызывает format(Course).");
    }

    private static class BaseFormatter {
        public Object format(Course course) {
            return "Base format: " + course.title();
        }
    }

    private static class SpecialFormatter extends BaseFormatter {
        @Override
        public String format(Course course) {
            return "Correct override: " + course.title();
        }

        public String format(SpecialCourse course) {
            return "Accidental overload: " + course.course().title();
        }
    }

    private record SpecialCourse(Course course) {
    }
}
