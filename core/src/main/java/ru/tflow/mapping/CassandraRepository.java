package ru.tflow.mapping;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import ru.tflow.mapping.exceptions.DuplicateKeyException;
import ru.tflow.mapping.utils.Tuple2;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static ru.tflow.mapping.utils.MappingUtils.*;
import static ru.tflow.mapping.utils.ReflectionUtils.*;

/**
 * User: erofeev
 * Date: 11/30/13
 * Time: 8:35 PM
 */
public interface CassandraRepository<E, K> extends MapperConfigurationProvider {

    /**
     * Find entity by it's keys
     *
     * @param key      Key
     * @param compound Entity compound keys values. Must be in order of sorted ascending composite keys
     * @return Entity
     * @throws DuplicateKeyException - if there is more than one entity with keys supplied
     */
    public default Optional<E> findOne(K key, Object... compound) throws DuplicateKeyException {
        List<E> result = find(key, compound);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        if (result.size() > 1) {
            throw new DuplicateKeyException("More than one record found", key, conf().metadata(getClass()).getEntityClass());
        }
        return Optional.of(result.get(0));
    }

    /**
     * Find several objects with keys provided. Using "where K in (...)" query
     *
     * @return List of found objects or empty list if none found.
     */
    public default List<E> findSeveral(K... keys) {
        String queryTemplate = "select * from %s.%s where %s in (%s)";

        Function<Object, String> format = conf().metadata(getClass()).getPrimaryKey().getFieldType().getOriginalType().isAssignableFrom(String.class)
            ? (o) -> "'" + o + "'" : Object::toString;

        ResultSet rs = conf().session().execute(String.format(queryTemplate,
            conf().keyspace(),
            conf().metadata(getClass()).getTable(),
            conf().metadata(getClass()).getPrimaryKey().getName(),
            Arrays.asList(keys).stream().map(format::apply).collect(Collectors.joining(", "))));

        return convert(rs, conf().metadata(getClass()));
    }

    /**
     * Find entity by it's keys
     *
     * @param key      Entity key
     * @param compound Entity compound keys values. Must be in order of sorted ascending composite keys
     * @return List of entity objects or empty list if none found
     */
    @SuppressWarnings("unchecked")
    public default List<E> find(K key, Object... compound) {

        Objects.requireNonNull(key, "Primary key cannot be empty when using find method.");

        final String queryKey = "find_" + compound.length;

        PreparedStatement statement = conf().getStatement(conf().metadata(getClass()).getEntityClass(), queryKey).orElseGet(() -> {
            String selectTemplate = "select * from %s.%s where %s=? %s";
            String compoundKeys = compound(conf().metadata(getClass()), compound);

            PreparedStatement stm = conf().session().prepare(String.format(selectTemplate,
                conf().keyspace(),
                conf().metadata(getClass()).getTable(),
                conf().metadata(getClass()).getPrimaryKey().getName(), compoundKeys));

            conf().putStatement(conf().metadata(getClass()).getEntityClass(), queryKey, stm);

            return stm;
        });

        ResultSet rs = conf().session().execute(bindKeys(statement, conf().metadata(getClass()), key, compound));

        return convert(rs, conf().metadata(getClass()));
    }

    /**
     * Find all entries in data table
     *
     * @return List of all entries in data table
     */
    @SuppressWarnings("unchecked")
    public default List<E> findAll(int limit) {

        final String queryKey = "findAll";

        PreparedStatement stm = conf().getStatement(conf().metadata(getClass()).getEntityClass(), queryKey).orElseGet(() -> {
            String selectTemplate = "select * from %s.%s limit ?";
            PreparedStatement _stm = conf().session().prepare(String.format(selectTemplate, conf().keyspace(), conf().metadata(getClass()).getTable()));
            conf().putStatement(conf().metadata(getClass()).getEntityClass(), queryKey, _stm);
            return _stm;
        });

        ResultSet set = conf().session().execute(stm.bind(limit));

        return convert(set, conf().metadata(getClass()));
    }

    /**
     * Save entity to database
     *
     * @param entity Entity to save
     * @throws DuplicateKeyException if there is already entity with such key in database
     */
    public default void save(E entity) throws DuplicateKeyException {

        final String queryKey = "save";

        BoundStatement stm = conf().getStatement(conf().metadata(getClass()).getEntityClass(), queryKey).orElseGet(() -> {
            String insertTemplate = "insert into %s.%s (%s) values (%s)";
            List<FieldMetadata> fm = conf().fields(getClass());
            String fields = fm.stream().map(FieldMetadata::getName).collect(joining(", "));
            String values = fm.stream().map(f -> "?").collect(joining(", "));
            String str = String.format(insertTemplate, conf().keyspace(), conf().metadata(getClass()).getTable(), fields, values);

            PreparedStatement _stm = conf().session().prepare(str);
            conf().putStatement(conf().metadata(getClass()).getEntityClass(), queryKey, _stm);
            return _stm;
        }).bind();

        PrimitiveIterator.OfInt counting = IntStream.range(0, conf().metadata(getClass()).getFields().size()).iterator();

        //Get raw setValue() method of bound statement
        Method setValue = findMethod(stm.getClass(), "setValue", int.class, ByteBuffer.class);
        setValue.setAccessible(true);

        conf().fields(getClass()).stream().map(f -> new Tuple2<>(counting.next(), f)).forEachOrdered(f -> {
            Object val = readField(f.getElement2().getField(), entity);
            ByteBuffer value = null;
            if (val != null) {
                value = f.getElement2().getFieldType().serialize(val);
            }
            invoke(setValue, stm, f.getElement1(), value);
        });
        conf().session().execute(stm);
    }

    /**
     * Delete entity from database
     *
     * @param key      Entity key
     * @param compound Entity compound keys
     */
    public default void delete(K key, Object... compound) {
        Objects.requireNonNull(key, "Key cannot be null");

        final String queryKey = "delete";

        PreparedStatement stm = conf().getStatement(conf().metadata(getClass()).getEntityClass(), queryKey).orElseGet(() -> {
            String deleteTemplate = "delete from %s.%s where %s=? %s";
            String compoundKeys = compound(conf().metadata(getClass()), compound);

            String query = String.format(deleteTemplate,
                conf().keyspace(),
                conf().metadata(getClass()).getTable(),
                conf().metadata(getClass()).getPrimaryKey().getName(),
                compoundKeys);

            PreparedStatement _stm = conf().session().prepare(query);
            conf().putStatement(conf().metadata(getClass()).getEntityClass(), queryKey, _stm);

            return _stm;

        });

        conf().session().execute(bindKeys(stm, conf().metadata(getClass()), key, compound));

    }

}
