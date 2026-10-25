package coursehub.demo;

import coursehub.model.Course;
import coursehub.storage.CourseBinaryStore;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Task05BinaryFormat {
    public static void run() throws IOException {
        System.out.println("\nЗадание 5. DataInputStream / DataOutputStream");
        Path directory = Files.createTempDirectory("coursehub-task05-");
        Path file = directory.resolve("courses.bin");
        List<Course> courses = List.of(new Course(101, "Java", 40, 8));
        CourseBinaryStore.write(file, courses);
        List<Course> restored = CourseBinaryStore.read(file);
        System.out.println("Восстановлено курсов: " + restored.size() + ", " + restored.getFirst());

        Path unknownVersion = directory.resolve("unknown.bin");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(unknownVersion))) {
            output.writeInt(CourseBinaryStore.MAGIC);
            output.writeInt(CourseBinaryStore.VERSION + 1);
            output.writeInt(0);
        }
        expectReadFailure(unknownVersion, "Неизвестная версия", "Неизвестная версия отклонена");

        byte[] bytes = Files.readAllBytes(file);
        Path truncated = directory.resolve("truncated.bin");
        Files.write(truncated, Arrays.copyOf(bytes, bytes.length - 1));
        expectReadFailure(truncated, "обрезан", "Обрезанный файл отклонён");
    }

    private static void expectReadFailure(Path file, String expectedMessage, String message) throws IOException {
        try {
            CourseBinaryStore.read(file);
            throw new AssertionError(message);
        } catch (IOException exception) {
            if (!exception.getMessage().contains(expectedMessage)) {
                throw exception;
            }
            System.out.println(message + ".");
        }
    }
}
