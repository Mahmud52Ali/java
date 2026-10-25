package coursehub.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Task04FilesLines {
    public static void run() throws IOException {
        System.out.println("\nЗадание 4. Files.lines");
        Path file = Files.createTempFile("coursehub-task04-", ".txt");
        Files.writeString(file, "Java\nАлгоритмы\n", StandardCharsets.UTF_8);
        System.out.println("Корректно прочитано строк: " + countLines(file));

        try {
            Stream<String> closedStream = returnClosedStream(file);
            closedStream.count();
            throw new AssertionError("Поток, закрытый try-with-resources, должен быть непригоден для чтения");
        } catch (IllegalStateException exception) {
            System.out.println("Показана ошибка: нельзя вернуть поток после закрытия его ресурса.");
        }
        System.out.println("Поток должен использоваться и закрываться внутри метода-владельца ресурса.");
    }

    private static long countLines(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines.count();
        }
    }

    private static Stream<String> returnClosedStream(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            return lines;
        }
    }
}
