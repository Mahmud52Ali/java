package coursehub.storage;

import coursehub.model.Course;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CourseBinaryStore {
    public static final int MAGIC = 0x43525331;
    public static final int VERSION = 1;
    private static final int MAX_COURSES = 1_000_000;

    private CourseBinaryStore() {
    }

    public static void write(Path destination, List<Course> courses) throws IOException {
        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(destination)))) {
            output.writeInt(MAGIC);
            output.writeInt(VERSION);
            output.writeInt(courses.size());
            for (Course course : courses) {
                output.writeLong(course.id());
                output.writeUTF(course.title());
                output.writeInt(course.durationHours());
                output.writeInt(course.completedHours());
            }
        }
    }

    public static List<Course> read(Path source) throws IOException {
        try (DataInputStream input = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(source)))) {
            int magic = input.readInt();
            if (magic != MAGIC) {
                throw new IOException("Неверная сигнатура бинарного каталога");
            }
            int version = input.readInt();
            if (version != VERSION) {
                throw new IOException("Неизвестная версия бинарного каталога: " + version);
            }
            int count = input.readInt();
            if (count < 0 || count > MAX_COURSES) {
                throw new IOException("Недопустимое количество курсов: " + count);
            }

            List<Course> courses = new ArrayList<>(count);
            try {
                for (int i = 0; i < count; i++) {
                    long id = input.readLong();
                    String title = input.readUTF();
                    int durationHours = input.readInt();
                    int completedHours = input.readInt();
                    courses.add(new Course(id, title, durationHours, completedHours));
                }
            } catch (IllegalArgumentException exception) {
                throw new IOException("Некорректная запись курса в бинарном каталоге", exception);
            }

            if (input.read() != -1) {
                throw new IOException("В бинарном каталоге есть лишние данные");
            }
            return List.copyOf(courses);
        } catch (EOFException exception) {
            throw new IOException("Бинарный каталог обрезан", exception);
        }
    }
}
