package coursehub.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/* Повторный save заменяет значение того же ID. findAll возвращает
   неизменяемый снимок коллекции; сами сущности не копируются. */
public final class InMemoryRepository<ID, T> implements Repository<ID, T> {
    private final Map<ID, T> entries = new LinkedHashMap<>();

    @Override
    public void save(ID id, T entity) {
        Objects.requireNonNull(id, "ID не должен быть null");
        Objects.requireNonNull(entity, "Сущность не должна быть null");
        entries.put(id, entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        Objects.requireNonNull(id, "ID не должен быть null");
        return Optional.ofNullable(entries.get(id));
    }

    @Override
    public List<T> findAll() {
        return List.copyOf(entries.values());
    }

    @Override
    public boolean deleteById(ID id) {
        Objects.requireNonNull(id, "ID не должен быть null");
        return entries.remove(id) != null;
    }
}
