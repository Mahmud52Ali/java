package coursehub.storage;

import java.util.List;

public record StreamingImportReport(
        long linesRead,
        long validRows,
        long invalidRows,
        List<ImportError> errors,
        boolean cancelled,
        boolean published) {

    public StreamingImportReport {
        errors = List.copyOf(errors);
    }

    public long omittedErrors() {
        return invalidRows - errors.size();
    }
}
