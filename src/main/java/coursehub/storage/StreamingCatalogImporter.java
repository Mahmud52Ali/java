package coursehub.storage;

import coursehub.model.Course;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class StreamingCatalogImporter {
    private StreamingCatalogImporter() {
    }

    public static StreamingImportReport importFile(
            Path source, Path destination, int maxStoredErrors, long cancelAfterErrors) throws IOException {
        if (maxStoredErrors < 0 || cancelAfterErrors < 1) {
            throw new IllegalArgumentException("Лимит ошибок не может быть отрицательным, порог отмены должен быть больше нуля");
        }

        Path absoluteDestination = destination.toAbsolutePath();
        Path parent = absoluteDestination.getParent();
        Files.createDirectories(parent);
        String prefix = absoluteDestination.getFileName().toString();
        if (prefix.length() < 3) {
            prefix = (prefix + "___").substring(0, 3);
        }
        Path stagingFile = Files.createTempFile(parent, prefix, ".import");
        boolean published = false;
        long linesRead = 0;
        long validRows = 0;
        long invalidRows = 0;
        boolean cancelled = false;
        List<ImportError> errors = new ArrayList<>();

        try {
            try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8);
                 BufferedWriter writer = Files.newBufferedWriter(stagingFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    linesRead++;
                    try {
                        Course course = CourseTextFormat.parseCourse(line);
                        writer.write(CourseTextFormat.formatCourse(course));
                        writer.newLine();
                        validRows++;
                    } catch (IllegalArgumentException exception) {
                        invalidRows++;
                        if (errors.size() < maxStoredErrors) {
                            errors.add(new ImportError(linesRead, exception.getMessage()));
                        }
                        if (invalidRows >= cancelAfterErrors) {
                            cancelled = true;
                            break;
                        }
                    }
                }
                writer.flush();
            }

            if (!cancelled) {
                try {
                    Files.move(stagingFile, absoluteDestination,
                            StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException exception) {
                    Files.move(stagingFile, absoluteDestination, StandardCopyOption.REPLACE_EXISTING);
                }
                published = true;
            }

            return new StreamingImportReport(
                    linesRead, validRows, invalidRows, errors, cancelled, published);
        } finally {
            if (!published) {
                Files.deleteIfExists(stagingFile);
            }
        }
    }
}
