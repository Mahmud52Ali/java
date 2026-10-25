package coursehub.storage;

import coursehub.model.Course;
import java.util.List;

public sealed interface CatalogImportResult
        permits CatalogImportResult.Success, CatalogImportResult.Failure {

    record Success(List<Course> courses) implements CatalogImportResult {
        public Success {
            courses = List.copyOf(courses);
        }
    }

    record Failure(List<ImportError> errors) implements CatalogImportResult {
        public Failure {
            errors = List.copyOf(errors);
        }
    }
}
