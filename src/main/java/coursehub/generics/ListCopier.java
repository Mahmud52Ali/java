package coursehub.generics;

import java.util.List;
import java.util.Objects;

public final class ListCopier {

    private ListCopier() {
    }

    public static <T> void copy(
            List<? extends T> source,
            List<? super T> destination
    ) {
        Objects.requireNonNull(source, "Источник не должен быть null");
        Objects.requireNonNull(
                destination,
                "Получатель не должен быть null"
        );

        for (T element : source) {
            destination.add(element);
        }
    }
}