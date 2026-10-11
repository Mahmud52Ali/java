package coursehub.graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* DFS: 1 — вершина в текущем пути, 2 — полностью обработана.
   Prerequisites добавляются в порядок раньше зависимого курса. */
public final class DependencyPlanner<T> {
    public PlanningResult<T> plan(DependencyGraph<T> graph) {
        Objects.requireNonNull(graph, "Граф не должен быть null");
        Map<T, Set<T>> dependencies = graph.snapshot();
        Map<T, Integer> states = new LinkedHashMap<>();
        List<T> path = new ArrayList<>();
        List<T> order = new ArrayList<>();

        for (T vertex : dependencies.keySet()) {
            List<T> cycle = visit(vertex, dependencies, states, path, order);
            if (!cycle.isEmpty()) {
                return new PlanningResult<>(List.of(), cycle);
            }
        }
        return new PlanningResult<>(order, List.of());
    }

    private List<T> visit(
            T vertex,
            Map<T, Set<T>> dependencies,
            Map<T, Integer> states,
            List<T> path,
            List<T> order
    ) {
        Integer state = states.get(vertex);
        if (state != null) {
            if (state == 1) {
                List<T> cycle = new ArrayList<>(path.subList(path.indexOf(vertex), path.size()));
                cycle.add(vertex);
                return cycle;
            }
            return List.of();
        }

        states.put(vertex, 1);
        path.add(vertex);
        for (T prerequisite : dependencies.get(vertex)) {
            List<T> cycle = visit(prerequisite, dependencies, states, path, order);
            if (!cycle.isEmpty()) {
                return cycle;
            }
        }
        path.remove(path.size() - 1);
        states.put(vertex, 2);
        order.add(vertex);
        return List.of();
    }
}
