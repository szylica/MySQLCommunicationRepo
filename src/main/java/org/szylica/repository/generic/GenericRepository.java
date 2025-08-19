package org.szylica.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    void insert(T entity);
    void update(ID id, T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    List<T> findAll(ID id);
}
