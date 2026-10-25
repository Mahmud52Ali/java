package coursehub.demo;

import coursehub.model.Course;
import coursehub.storage.UnsupportedFormatVersionException;
import coursehub.storage.VersionedCourseStore;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Task09VersionedFormat {
    public static void run() throws IOException {
        System.out.println("\nЗадание 9. Версия схемы, checksum и миграция");
        Path directory = Files.createTempDirectory("coursehub-task09-");
        Course course = new Course(101, "Java", 40, 8);

        Path currentFile = directory.resolve("v2.bin");
        VersionedCourseStore.writeV2(currentFile, List.of(course));
        System.out.println("Версия 2 сохранила прогресс: "
                + VersionedCourseStore.read(currentFile).getFirst().completedHours() + " часов.");

        Path oldFile = directory.resolve("v1.bin");
        VersionedCourseStore.writeLegacyVersion1(oldFile, List.of(course));
        Course migrated = VersionedCourseStore.read(oldFile).getFirst();
        System.out.println("Версия 1 мигрирована в модель версии 2; новый progress по умолчанию: "
                + migrated.completedHours() + " часов.");

        Path futureFile = directory.resolve("future.bin");
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(futureFile))) {
            output.writeInt(VersionedCourseStore.MAGIC);
            output.writeInt(VersionedCourseStore.VERSION_2 + 1);
        }
        try {
            VersionedCourseStore.read(futureFile);
            throw new AssertionError("Неизвестная будущая версия должна быть отклонена");
        } catch (UnsupportedFormatVersionException exception) {
            System.out.println("Неизвестная версия отклонена до разбора содержимого.");
        }

        byte[] bytes = Files.readAllBytes(currentFile);
        bytes[12] ^= 1;
        Path corruptedFile = directory.resolve("corrupted.bin");
        Files.write(corruptedFile, Arrays.copyOf(bytes, bytes.length));
        try {
            VersionedCourseStore.read(corruptedFile);
            throw new AssertionError("Повреждённая контрольная сумма должна отклонить файл");
        } catch (IOException exception) {
            if (!exception.getMessage().contains("Контрольная сумма не совпадает")) {
                throw exception;
            }
            System.out.println("Повреждение найдено checksum до создания и публикации списка курсов.");
        }
    }
}
