package coursehub.demo;

import coursehub.model.Course;
import coursehub.model.CourseId;
import coursehub.repository.InMemoryRepository;
import coursehub.repository.Repository;

import java.util.List;

public class Task12Repository {
    public static void run() {
        System.out.println("\nЗадание 12. Generic repository");
        Repository<CourseId, Course> repository = new InMemoryRepository<>();
        CourseId id = new CourseId(101);
        repository.save(id, new Course(101, "Java", 40));
        System.out.println("Найден: " + repository.findById(id).orElseThrow().title());
        System.out.println("Несуществующий ID: " + repository.findById(new CourseId(999)));

        List<Course> snapshot = repository.findAll();
        repository.save(id, new Course(101, "Java обновлённый", 50));
        System.out.println("Повторный save заменяет курс: " + repository.findById(id).orElseThrow().title());
        System.out.println("Старый снимок: " + snapshot.get(0).title());
        System.out.println("Число записей: " + repository.findAll().size());

        try {
            snapshot.clear();
        } catch (UnsupportedOperationException exception) {
            System.out.println("Снимок нельзя изменить");
        }
        try {
            repository.save(id, null);
        } catch (NullPointerException exception) {
            System.out.println("Null запрещён: " + exception.getMessage());
        }
        System.out.println("Удаление существующего: " + repository.deleteById(id));
        System.out.println("Повторное удаление: " + repository.deleteById(id));
        System.out.println("Репозиторий после удаления: " + repository.findAll());
    }
}
