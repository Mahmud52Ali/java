package coursehub.demo;

import coursehub.generics.Result;
import coursehub.model.Course;

public class Task06GenericResult {

    public static void run() {
        System.out.println("\nЗадание 6. Generic Result<T>");

        Result<Course> courseResult = Result.success(
                new Course(101, "Java", 40)
        );

        if (courseResult.isSuccess()) {
            Course course = courseResult.value();

            System.out.println(
                    "Успех: найден курс " + course.title()
            );
        }

        Result<String> messageResult =
                Result.success("Регистрация завершена");

        System.out.println("Сообщение: " + messageResult.value());

        Result<Course> failedResult =
                Result.failure("Курс не найден");

        if (!failedResult.isSuccess()) {
            System.out.println("Ошибка: " + failedResult.error());
        }

        try {
            failedResult.value();
        } catch (IllegalStateException exception) {
            System.out.println(exception.getMessage());
        }

        try {
            Result.<Course>success(null);
        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Недопустимый результат: " + exception.getMessage()
            );
        }

        try {
            Result.<Course>failure(" ");
        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Недопустимая ошибка: " + exception.getMessage()
            );
        }

        /* Если раскомментировать эти строки, компилятор запретит смешение типов:
           Result<String> wrongResult = courseResult;
           String wrongValue = courseResult.value();
        */
    }
}
