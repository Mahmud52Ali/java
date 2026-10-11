package coursehub.model;

import java.util.Objects;

public record Tag(String value) {
    public Tag {
        Objects.requireNonNull(value, "Тег не должен быть null");
    }
}
