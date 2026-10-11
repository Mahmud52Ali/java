package coursehub.index;

import coursehub.model.CourseId;
import coursehub.model.Tag;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* Добавление того же курса заменяет его набор тегов. Пустой запрос
   пересечения возвращает пустой результат. Снимки не изменяют индекс. */
public final class TagIndex {
    private final Map<Tag, Set<CourseId>> index = new LinkedHashMap<>();

    public void addCourse(CourseId id, Collection<Tag> tags) {
        Objects.requireNonNull(id, "ID не должен быть null");
        Objects.requireNonNull(tags, "Теги не должны быть null");
        Set<Tag> uniqueTags = new LinkedHashSet<>(tags);
        uniqueTags.forEach(tag -> Objects.requireNonNull(tag, "Тег не должен быть null"));

        removeCourse(id);
        for (Tag tag : uniqueTags) {
            index.computeIfAbsent(tag, key -> new LinkedHashSet<>()).add(id);
        }
    }

    public void removeCourse(CourseId id) {
        Objects.requireNonNull(id, "ID не должен быть null");
        index.entrySet().removeIf(entry -> {
            entry.getValue().remove(id);
            return entry.getValue().isEmpty();
        });
    }

    public Set<CourseId> findAll(Collection<Tag> tags) {
        Objects.requireNonNull(tags, "Теги не должны быть null");
        Set<Tag> query = new LinkedHashSet<>(tags);
        query.forEach(tag -> Objects.requireNonNull(tag, "Тег не должен быть null"));
        if (query.isEmpty()) {
            return Set.of();
        }

        Set<CourseId> result = null;
        for (Tag tag : query) {
            Set<CourseId> courses = index.get(tag);
            if (courses == null) {
                return Set.of();
            }
            if (result == null) {
                result = new LinkedHashSet<>(courses);
            } else {
                result.retainAll(courses);
            }
            if (result.isEmpty()) {
                return Set.of();
            }
        }
        return Collections.unmodifiableSet(result);
    }

    public Map<Tag, Set<CourseId>> snapshot() {
        Map<Tag, Set<CourseId>> copy = new LinkedHashMap<>();
        index.forEach((tag, ids) -> copy.put(
                tag, Collections.unmodifiableSet(new LinkedHashSet<>(ids))
        ));
        return Collections.unmodifiableMap(copy);
    }
}
