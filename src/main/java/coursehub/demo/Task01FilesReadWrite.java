package coursehub.demo;

import coursehub.model.Course;
import coursehub.storage.CourseTextFormat;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

public class Task01FilesReadWrite {
    public static void run() throws IOException {
        System.out.println("\nЗадание 1. Files.readString / writeString");
        Path directory = Files.createTempDirectory("coursehub-task01-");
        Path file = directory.resolve("courses.txt");
        List<Course> courses = List.of(new Course(101, "Java Основы", 40, 8));

        Files.writeString(file, CourseTextFormat.formatCatalog(courses), StandardCharsets.UTF_8);
        String saved = Files.readString(file, StandardCharsets.UTF_8);
        System.out.println(saved.strip());

        try {
            Files.readString(directory.resolve("missing.txt"), StandardCharsets.UTF_8);
            throw new AssertionError("Отсутствующий файл должен вызвать ошибку");
        } catch (NoSuchFileException exception) {
            System.out.println("Отсутствующий файл обработан: чтение не удалось.");
        }

        expectIOException(() -> Files.readString(directory, StandardCharsets.UTF_8), "Чтение каталога вместо файла");
        expectIOException(() -> Files.writeString(directory, "text", StandardCharsets.UTF_8), "Запись в каталог вместо файла");
    }

    private static void expectIOException(IoAction action, String description) throws IOException {
        try {
            action.run();
            throw new AssertionError(description + " должна завершиться IOException");
        } catch (IOException exception) {
            System.out.println(description + " корректно отклонена.");
        }
    }

    @FunctionalInterface
    private interface IoAction {
        void run() throws IOException;
    }
}
