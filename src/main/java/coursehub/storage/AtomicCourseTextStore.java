package coursehub.storage;

import coursehub.model.Course;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class AtomicCourseTextStore {
    private AtomicCourseTextStore() {
    }

    public static boolean writeAtomically(Path destination, List<Course> courses) throws IOException {
        Path absoluteDestination = destination.toAbsolutePath();
        Path parent = absoluteDestination.getParent();
        Files.createDirectories(parent);

        String prefix = absoluteDestination.getFileName().toString();
        if (prefix.length() < 3) {
            prefix = (prefix + "___").substring(0, 3);
        }
        Path temporaryFile = Files.createTempFile(parent, prefix, ".tmp");
        boolean moved = false;

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                writer.write(CourseTextFormat.formatCatalog(courses));
                writer.flush();
            }

            try {
                Files.move(temporaryFile, absoluteDestination,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                moved = true;
                return true;
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, absoluteDestination, StandardCopyOption.REPLACE_EXISTING);
                moved = true;
                return false;
            }
        } finally {
            if (!moved) {
                Files.deleteIfExists(temporaryFile);
            }
        }
    }
}
