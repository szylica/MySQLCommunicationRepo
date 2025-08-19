package org.szylica.repository;

import lombok.RequiredArgsConstructor;
import org.atteo.evo.inflector.English;
import org.jdbi.v3.core.Jdbi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {

    protected final Jdbi jdbi;

    @SuppressWarnings("unchecked")
    private final Class<T> entityType = (Class<T>) getGenericTypeClass();

    private final String tableName = English.plural(entityType.getSimpleName().toLowerCase());

    @Override
    public void insert(T entity) {
        var sql = "insert into " + tableName + "  values (:entity)";
        jdbi.useHandle(handle -> handle
                .createUpdate(sql)
                .bindBean("entity", entity)
                .execute()
        );
    }

    @Override
    public void update(ID id, T entity) {

    }

    @Override
    public void delete(ID id) {

    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.empty();
    }

    @Override
    public List<T> findAll(ID id) {
        return List.of();
    }

    private Class<?> getGenericTypeClass() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        throw new IllegalArgumentException("Class does not have a generic superclass");
    }
}
