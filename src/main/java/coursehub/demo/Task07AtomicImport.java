package coursehub.demo;

import coursehub.storage.CatalogImportResult;
import coursehub.storage.CatalogImporter;
import coursehub.storage.ImportError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Task07AtomicImport {
    public static void run() throws IOException {
        System.out.println("\nЗадание 7. Импорт повреждённого файла целиком или никак");
        Path file = Files.createTempFile("coursehub-task07-", ".txt");
        Files.writeString(file, "101;Java;40;0\nплохая строка\n102;SQL;30;3\n", StandardCharsets.UTF_8);

        CatalogImportResult result = CatalogImporter.importAll(file);
        if (result instanceof CatalogImportResult.Failure failure) {
            for (ImportError error : failure.errors()) {
                System.out.println("Строка " + error.lineNumber() + ": " + error.message());
            }
            System.out.println("Импорт не опубликован: один плохой ряд отменяет весь каталог.");
        } else if (result instanceof CatalogImportResult.Success success) {
            System.out.println("Импортировано целиком курсов: " + success.courses().size());
        }
    }
}
