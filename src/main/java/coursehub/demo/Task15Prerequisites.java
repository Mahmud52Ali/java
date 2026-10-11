package coursehub.demo;

import coursehub.graph.DependencyGraph;
import coursehub.graph.DependencyPlanner;
import coursehub.graph.PlanningResult;
import coursehub.model.CourseId;

import java.util.List;

public class Task15Prerequisites {
    public static void run() {
        System.out.println("\nЗадание 15. Планировщик prerequisites");
        CourseId basics = new CourseId(101);
        CourseId oop = new CourseId(102);
        CourseId collections = new CourseId(103);
        CourseId project = new CourseId(104);

        DependencyGraph<CourseId> graph = new DependencyGraph<>();
        for (CourseId id : List.of(basics, oop, collections, project)) {
            graph.addVertex(id);
        }
        graph.addDependency(oop, basics);
        graph.addDependency(collections, oop);
        graph.addDependency(project, collections);

        DependencyPlanner<CourseId> planner = new DependencyPlanner<>();
        PlanningResult<CourseId> result = planner.plan(graph);
        System.out.println("Порядок: " + result.order().stream().map(CourseId::value).toList());
        System.out.println("Повторный запуск совпадает: " + result.equals(planner.plan(graph)));

        graph.addDependency(basics, collections);
        PlanningResult<CourseId> cyclic = planner.plan(graph);
        System.out.println("Есть цикл: " + cyclic.hasCycle());
        System.out.println("Путь цикла: " + cyclic.cycle().stream().map(CourseId::value).toList());
        System.out.println("Порядок при цикле не выдаётся: " + cyclic.order());

        try {
            graph.addDependency(project, new CourseId(999));
        } catch (IllegalArgumentException exception) {
            System.out.println("Неизвестная вершина: " + exception.getMessage());
        }
    }
}
