package coursehub.demo;

import coursehub.model.Course;
import coursehub.storage.AtomicCourseTextStore;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Task06AtomicMove {
    public static void run() throws IOException {
        System.out.println("\nЗадание 6. Временный файл и move");
        Path directory = Files.createTempDirectory("coursehub-task06-");
        Path destination = directory.resolve("courses.txt");
        Files.writeString(destination, "старый каталог", StandardCharsets.UTF_8);

        boolean atomic = AtomicCourseTextStore.writeAtomically(
                destination, List.of(new Course(101, "Java", 40, 8)));
        System.out.println("Новый файл записан и заменил старый. Atomic move поддержан: " + atomic);
        System.out.println(Files.readString(destination, StandardCharsets.UTF_8).strip());
    }
}
