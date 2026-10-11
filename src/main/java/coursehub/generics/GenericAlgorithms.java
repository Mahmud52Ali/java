package coursehub.generics;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public final class GenericAlgorithms {

    private GenericAlgorithms() {
    }

    public static <T> T firstOrThrow(List<T> list) {
        Objects.requireNonNull(list, "Список не должен быть null");

        if (list.isEmpty()) {
            throw new NoSuchElementException("Список пуст");
        }

        return list.get(0);
    }

    public static <T> void swap(List<T> list, int first, int second) {
        Objects.requireNonNull(list, "Список не должен быть null");

        Objects.checkIndex(first, list.size());
        Objects.checkIndex(second, list.size());

        T temporary = list.get(first);

        list.set(first, list.get(second));
        list.set(second, temporary);
    }

    public static <T, K> Map<K, T> indexBy(
            List<T> list,
            Function<T, K> keyExtractor
    ) {
        Objects.requireNonNull(list, "Список не должен быть null");
        Objects.requireNonNull(
                keyExtractor,
                "Функция получения ключа не должна быть null"
        );

        Map<K, T> index = new LinkedHashMap<>();

        for (T element : list) {
            Objects.requireNonNull(element, "Элемент не должен быть null");

            K key = Objects.requireNonNull(
                    keyExtractor.apply(element),
                    "Ключ не должен быть null"
            );

            if (index.containsKey(key)) {
                throw new IllegalArgumentException(
                        "Повторный ключ: " + key
                );
            }

            index.put(key, element);
        }

        return index;
    }

    public static <T extends Comparable<? super T>> T max(List<T> list) {
        Objects.requireNonNull(list, "Список не должен быть null");

        if (list.isEmpty()) {
            throw new NoSuchElementException("Список пуст");
        }

        T maximum = Objects.requireNonNull(
                list.get(0),
                "Элемент не должен быть null"
        );

        for (T element : list) {
            Objects.requireNonNull(element, "Элемент не должен быть null");

            if (element.compareTo(maximum) > 0) {
                maximum = element;
            }
        }

        return maximum;
    }
    
}