package coursehub.storage;

import java.io.IOException;

public class UnsupportedFormatVersionException extends IOException {
    public UnsupportedFormatVersionException(int version) {
        super("Неподдерживаемая версия формата: " + version);
    }
}
