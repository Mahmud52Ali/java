package coursehub.demo;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Task03TryWithResources {
    public static void run() {
        System.out.println("\nЗадание 3. Try-with-resources");
        List<String> closeOrder = new ArrayList<>();

        try (Reader reader = new TrackingReader("данные", closeOrder);
             Writer writer = new TrackingWriter(closeOrder)) {
            writer.write(reader.read());
            throw new IOException("Искусственная ошибка в середине pipeline");
        } catch (IOException exception) {
            System.out.println("Возникла ожидаемая ошибка: " + exception.getMessage());
        }

        if (!closeOrder.equals(List.of("writer", "reader"))) {
            throw new AssertionError("Ресурсы закрыты не в обратном порядке: " + closeOrder);
        }
        System.out.println("Порядок close: " + closeOrder + ". Оба ресурса закрыты.");
    }

    private static final class TrackingReader extends StringReader {
        private final List<String> closeOrder;

        private TrackingReader(String text, List<String> closeOrder) {
            super(text);
            this.closeOrder = closeOrder;
        }

        @Override
        public void close() {
            closeOrder.add("reader");
            super.close();
        }
    }

    private static final class TrackingWriter extends StringWriter {
        private final List<String> closeOrder;

        private TrackingWriter(List<String> closeOrder) {
            this.closeOrder = closeOrder;
        }

        @Override
        public void close() throws IOException {
            closeOrder.add("writer");
            super.close();
        }
    }
}
