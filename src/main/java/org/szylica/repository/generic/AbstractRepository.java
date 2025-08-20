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
        var nonNullFields = getNonNullFields(entity);
        ;
        if (nonNullFields.isEmpty()) {
            throw new IllegalArgumentException("No fields to insert");
        }
        var sql = createInsertSql(nonNullFields);


        jdbi.useHandle(handle -> {
            var update = handle.createUpdate(sql);
            bindFieldsToUpdate(entity, update, nonNullFields);
            update.execute();
        });
    }

    @Override
    public void update(ID id, T entity) {

        var nonNullFields = getNonNullFields(entity);

        if (nonNullFields.isEmpty()) {
            throw new IllegalArgumentException("No fields to update");
        }

        var sql = createUpdateSql(nonNullFields);
        System.out.println(sql);

        jdbi.useHandle(handle -> {
                    var update = handle.createUpdate(sql);
                    bindFieldsToUpdate(entity, update, nonNullFields);
                    update.bind("id", id);
                    update.execute();
                }
        );
    }

    @Override
    public void delete(ID id) {
        var sql = "delete  from %s where id = :id".formatted(tableName);
        jdbi.useHandle(handle -> {
                    var delete = handle.createUpdate(sql);
                    delete.bind("id", id);
                    delete.execute();
                }
        );
    }

    @Override
    public Optional<T> findById(ID id) {
        var sql = "select * from %s where id = :id".formatted(tableName);
        return jdbi.withHandle(handle -> handle
                .createQuery(sql)
                .bind("id", id)
                .mapToBean(entityType)
                .findFirst());
    }

    @Override
    public List<T> findAll() {
        var sql = "select * from %s".formatted(tableName);
        return jdbi.withHandle(handle -> handle
                .createQuery(sql)
                .mapToBean(entityType)
                .list());
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

    private void bindFieldsToUpdate(T entity, Update update, List<String> fields) {
        for (var fieldName : fields) {
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

    private List<String> getNonNullFields(T entity) {
        return fieldNamesWithoutId
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
                .toList();
    }

    private String createInsertSql(List<String> nonNullFields) {
        var columnsNames = nonNullFields
                .stream()
                .map(this::getColumnName)
                .collect(Collectors.joining(", "));

        var placeholders = nonNullFields
                .stream()
                .map(fieldName -> ":" + fieldName)
                .collect(Collectors.joining(", "));

        return "insert into %s ( %s ) values ( %s )".formatted(tableName, columnsNames, placeholders);
    }

    private String createUpdateSql(List<String> nonNullFields) {
        var setClause = nonNullFields
                .stream()
                .map(fieldName -> getColumnName(fieldName) + " = :" + fieldName)
                .collect(Collectors.joining(", "));

        return "update %s set %s".formatted(tableName, setClause);
    }

}
