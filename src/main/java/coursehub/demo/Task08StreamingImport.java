package coursehub.demo;

import coursehub.storage.StreamingCatalogImporter;
import coursehub.storage.StreamingImportReport;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Task08StreamingImport {
    public static void run() throws IOException {
        System.out.println("\nЗадание 8. Потоковый импорт большого файла");
        System.out.println("Файл читается построчно; весь каталог и список Course в память не загружаются.");
        Path directory = Files.createTempDirectory("coursehub-task08-");
        Path source = directory.resolve("large-source.txt");
        Path destination = directory.resolve("published.txt");
        Files.writeString(source,
                "101;Java;40;0\nплохая строка\n102;SQL;30;3\nтоже ошибка\n103;C++;20;1\n",
                StandardCharsets.UTF_8);

        StreamingImportReport report = StreamingCatalogImporter.importFile(source, destination, 1, 3);
        printReport(report);
        System.out.println("Число сохранённых сообщений об ошибках ограничено одним.");

        Path cancelledSource = directory.resolve("cancelled-source.txt");
        Files.writeString(cancelledSource, "ошибка 1\nошибка 2\n104;Go;10;0\n", StandardCharsets.UTF_8);
        String previousFile = Files.readString(destination, StandardCharsets.UTF_8);
        StreamingImportReport cancelled = StreamingCatalogImporter.importFile(cancelledSource, destination, 2, 2);
        printReport(cancelled);
        if (!cancelled.cancelled() || cancelled.published()
                || !previousFile.equals(Files.readString(destination, StandardCharsets.UTF_8))) {
            throw new AssertionError("Отмена не должна заменять опубликованный каталог");
        }
        System.out.println("При достижении порога временный результат удалён, прежний каталог сохранён.");
    }

    private static void printReport(StreamingImportReport report) {
        System.out.println("Строк прочитано: " + report.linesRead()
                + ", корректных: " + report.validRows()
                + ", ошибочных: " + report.invalidRows()
                + ", отменено: " + report.cancelled()
                + ", опубликовано: " + report.published()
                + ", не сохранено сообщений об ошибках: " + report.omittedErrors());
    }
}
