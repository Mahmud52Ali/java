package coursehub.graph;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* Ребро course -> prerequisite. Обе вершины нужно добавить заранее.
   LinkedHashMap и LinkedHashSet фиксируют порядок обхода. */
public final class DependencyGraph<T> {
    private final Map<T, Set<T>> prerequisites = new LinkedHashMap<>();

    public void addVertex(T vertex) {
        Objects.requireNonNull(vertex, "Вершина не должна быть null");
        prerequisites.computeIfAbsent(vertex, key -> new LinkedHashSet<>());
    }

    public void addDependency(T course, T prerequisite) {
        Objects.requireNonNull(course, "Курс не должен быть null");
        Objects.requireNonNull(prerequisite, "Prerequisite не должен быть null");
        if (!prerequisites.containsKey(course) || !prerequisites.containsKey(prerequisite)) {
            throw new IllegalArgumentException("Отсутствует вершина зависимости");
        }
        prerequisites.get(course).add(prerequisite);
    }

    public Map<T, Set<T>> snapshot() {
        Map<T, Set<T>> copy = new LinkedHashMap<>();
        prerequisites.forEach((vertex, dependencies) -> copy.put(
                vertex, Collections.unmodifiableSet(new LinkedHashSet<>(dependencies))
        ));
        return Collections.unmodifiableMap(copy);
    }
}
