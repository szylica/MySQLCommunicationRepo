package org.szylica.repository.generic;

import lombok.RequiredArgsConstructor;
import org.atteo.evo.inflector.English;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Update;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {

    protected final Jdbi jdbi;
    private final Class<T> entityType;
    private final String tableName;
    private final List<String> fieldNamesWithoutId;
    private final Map<String, String> fieldColumnMap;

    @SuppressWarnings("unchecked")
    protected AbstractRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
        this.entityType = (Class<T>) getGenericTypeClass();
        this.tableName = English.plural(entityType.getSimpleName().toLowerCase());
        this.fieldNamesWithoutId = getFieldNamesWithoutId();
        this.fieldColumnMap = getFieldColumnMap();
        registerMappers();
    }

    protected abstract void registerMappers();

    protected Map<String, String> getFieldColumnMap() {
        return Map.of();
    }

    @Override
    public void insert(T entity) {

        var sql = "insert into %s ( %s ) values ( %s )".formatted(
                tableName,
                String.join(", ", getColumnNamesWithoutId()),
                String.join(", ", getFieldPlaceholdersWithoutId())
        );

        System.out.println(sql);

        jdbi.useHandle(handle -> {
            var update = handle.createUpdate(sql);
            bindFieldsToUpdate(entity, update);
            update.execute();
        });
    }

    @Override
    public void update(ID id, T entity) {

        var setClause = fieldNamesWithoutId
                .stream()
                .filter(fieldName -> {
                    try {
                        var field = entityType.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        return field.get(entity) != null;
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .map(fieldName -> getColumnName(fieldName) + " = :" + fieldName)
                .collect(Collectors.joining(", "));

        if (setClause.isEmpty()) {
            throw new IllegalArgumentException("No fields to update");
        }

        var sql = "update %s set %s where id = :id".formatted(tableName, setClause);
        System.out.println(sql);

        jdbi.useHandle(handle -> {
                    var update = handle.createUpdate(sql);
                    bindFieldsToUpdate(entity, update);
                    update.bind("id", id);
                    update.execute();
                }
        );
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

    private List<String> getFieldNamesWithoutId() {
        return Arrays
                .stream(entityType.getDeclaredFields())
                .map(Field::getName)
                .filter(name -> !name.equalsIgnoreCase("id"))
                .toList();
    }

    private List<String> getColumnNamesWithoutId() {
        return fieldNamesWithoutId
                .stream()
                .map(this::getColumnName)
                .toList();
    }

    private List<String> getFieldPlaceholdersWithoutId() {
        return fieldNamesWithoutId
                .stream()
                .map(name -> ":" + name)
                .toList();
    }

    private String getColumnName(String fieldName) {
        return fieldColumnMap.getOrDefault(fieldName, fieldName);
    }

    private void bindFieldsToUpdate(T entity, Update update) {
        for (var fieldName : fieldNamesWithoutId) {
            try {
                var field = entityType.getDeclaredField(fieldName);
                field.setAccessible(true);
                var value = field.get(entity);
                if (value != null) {
                    update.bind(fieldName, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
