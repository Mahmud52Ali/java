package coursehub.demo;

import coursehub.model.Course;
import coursehub.storage.CourseTextFormat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Task02BufferedReaderWriter {
    public static void run() throws IOException {
        System.out.println("\nЗадание 2. BufferedReader / BufferedWriter");
        Path file = Files.createTempFile("coursehub-task02-", ".txt");
        List<Course> courses = List.of(
                new Course(101, "Java Основы", 40, 8),
                new Course(102, "Алгоритмы", 32, 4));

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(CourseTextFormat.formatCatalog(courses));
        }

        long lineCount = 0;
        long characterCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                characterCount += line.codePointCount(0, line.length());
            }
        }

        System.out.println("Строк: " + lineCount + ", символов Unicode: " + characterCount);
        System.out.println("На границе файла декодер переводит UTF-8 bytes в символы Java; writer выполняет обратное преобразование.");
    }
}
