package coursehub.graph;

import java.util.List;

public record PlanningResult<T>(List<T> order, List<T> cycle) {
    public PlanningResult {
        order = List.copyOf(order);
        cycle = List.copyOf(cycle);
        if (!order.isEmpty() && !cycle.isEmpty()) {
            throw new IllegalArgumentException("Результат содержит либо порядок, либо цикл");
        }
    }

    public boolean hasCycle() {
        return !cycle.isEmpty();
    }
}
