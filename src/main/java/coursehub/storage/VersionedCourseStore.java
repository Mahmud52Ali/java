package coursehub.storage;

import coursehub.model.Course;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public final class VersionedCourseStore {
    public static final int MAGIC = 0x43525632;
    public static final int VERSION_1 = 1;
    public static final int VERSION_2 = 2;
    private static final int CHECKSUM_SIZE = 32;
    private static final int MAX_PAYLOAD_SIZE = 64 * 1024 * 1024;
    private static final int MAX_COURSES = 1_000_000;

    private VersionedCourseStore() {
    }

    public static void writeV2(Path destination, List<Course> courses) throws IOException {
        write(destination, courses, VERSION_2);
    }

    public static void writeLegacyVersion1(Path destination, List<Course> courses) throws IOException {
        write(destination, courses, VERSION_1);
    }

    public static List<Course> read(Path source) throws IOException {
        try (DataInputStream input = new DataInputStream(Files.newInputStream(source))) {
            int magic = input.readInt();
            if (magic != MAGIC) {
                throw new IOException("Неверная сигнатура версионируемого файла");
            }
            int version = input.readInt();
            if (version != VERSION_1 && version != VERSION_2) {
                throw new UnsupportedFormatVersionException(version);
            }
            int payloadSize = input.readInt();
            if (payloadSize < 0 || payloadSize > MAX_PAYLOAD_SIZE) {
                throw new IOException("Недопустимый размер данных: " + payloadSize);
            }
            byte[] payload = input.readNBytes(payloadSize);
            if (payload.length != payloadSize) {
                throw new EOFException("Данные каталога обрезаны");
            }
            byte[] checksum = input.readNBytes(CHECKSUM_SIZE);
            if (checksum.length != CHECKSUM_SIZE) {
                throw new EOFException("Контрольная сумма обрезана");
            }
            if (input.read() != -1) {
                throw new IOException("После контрольной суммы есть лишние данные");
            }
            if (!MessageDigest.isEqual(checksum, sha256(payload))) {
                throw new IOException("Контрольная сумма не совпадает: файл повреждён");
            }
            return decodePayload(payload, version);
        } catch (EOFException exception) {
            throw new IOException("Версионируемый файл обрезан", exception);
        }
    }

    private static void write(Path destination, List<Course> courses, int version) throws IOException {
        byte[] payload = encodePayload(courses, version);
        byte[] checksum = sha256(payload);
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(destination))) {
            output.writeInt(MAGIC);
            output.writeInt(version);
            output.writeInt(payload.length);
            output.write(payload);
            output.write(checksum);
        }
    }

    private static byte[] encodePayload(List<Course> courses, int version) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeInt(courses.size());
            for (Course course : courses) {
                output.writeLong(course.id());
                output.writeUTF(course.title());
                output.writeInt(course.durationHours());
                if (version == VERSION_2) {
                    output.writeInt(course.completedHours());
                }
            }
        }
        return bytes.toByteArray();
    }

    private static List<Course> decodePayload(byte[] payload, int version) throws IOException {
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(payload))) {
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
                    int completedHours = version == VERSION_1 ? 0 : input.readInt();
                    courses.add(new Course(id, title, durationHours, completedHours));
                }
            } catch (IllegalArgumentException exception) {
                throw new IOException("Некорректная запись курса", exception);
            }
            if (input.read() != -1) {
                throw new IOException("В данных каталога есть лишние байты");
            }
            return List.copyOf(courses);
        } catch (EOFException exception) {
            throw new IOException("Данные каталога обрезаны", exception);
        }
    }

    private static byte[] sha256(byte[] value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 недоступен", exception);
        }
    }
}
