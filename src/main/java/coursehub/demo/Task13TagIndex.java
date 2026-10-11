package coursehub.demo;

import coursehub.index.TagIndex;
import coursehub.model.CourseId;
import coursehub.model.Tag;

import java.util.List;

public class Task13TagIndex {
    public static void run() {
        System.out.println("\nЗадание 13. Обратный индекс");
        Tag java = new Tag("java");
        Tag backend = new Tag("backend");
        Tag data = new Tag("data");
        TagIndex index = new TagIndex();
        index.addCourse(new CourseId(101), List.of(java, backend));
        index.addCourse(new CourseId(102), List.of(java));
        index.addCourse(new CourseId(103), List.of(data));
        System.out.println("Индекс: " + index.snapshot());
        System.out.println("Java И backend: " + index.findAll(List.of(java, backend)));
        System.out.println("Неизвестный тег: " + index.findAll(List.of(new Tag("unknown"))));

        index.removeCourse(new CourseId(101));
        System.out.println("После удаления курса 101: " + index.snapshot());
        System.out.println("Пустой тег backend удалён: " + !index.snapshot().containsKey(backend));

        index.addCourse(new CourseId(102), List.of(data));
        System.out.println("После замены тегов курса 102: " + index.snapshot());
        index.removeCourse(new CourseId(102));
        index.removeCourse(new CourseId(103));
        System.out.println("После удаления всех курсов: " + index.snapshot());
    }
}
