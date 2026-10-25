package coursehub.storage;

import coursehub.model.Course;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CatalogImporter {
    private CatalogImporter() {
    }

    public static CatalogImportResult importAll(Path source) throws IOException {
        List<Course> pendingCourses = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();
        long lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    pendingCourses.add(CourseTextFormat.parseCourse(line));
                } catch (IllegalArgumentException exception) {
                    errors.add(new ImportError(lineNumber, exception.getMessage()));
                }
            }
        }

        if (!errors.isEmpty()) {
            return new CatalogImportResult.Failure(errors);
        }
        return new CatalogImportResult.Success(pendingCourses);
    }
}
