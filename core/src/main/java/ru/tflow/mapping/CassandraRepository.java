package ru.tflow.mapping;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import ru.tflow.mapping.exceptions.DuplicateKeyException;
import ru.tflow.mapping.utils.MappingUtils;
import ru.tflow.mapping.utils.Tuple2;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static ru.tflow.mapping.utils.ReflectionUtils.*;

/**
 * User: erofeev
 * Date: 11/30/13
 * Time: 8:35 PM
 */
public interface CassandraRepository<E, K> extends MapperConfigurationProvider {

    /**
     * Create bound statement from query
     *
     * @param query Query string
     * @return BoundStatement object
     */
    default BoundStatement boundStatement(String query) {
        return new BoundStatement(configuration().session().prepare(query));
    }

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
            throw new DuplicateKeyException("More than one record found", key, configuration().metadata(getClass()).getEntityClass());
        }
        return Optional.of(result.get(0));
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

        String selectTemplate = "select * from %s.%s where %s=? %s";
        String compoundKeys = MappingUtils.compound(configuration().metadata(getClass()), compound);
        Object[] params = MappingUtils.parameters(configuration().metadata(getClass()), key, compound);

        ResultSet rs = configuration().session().execute(boundStatement(String.format(selectTemplate,
            configuration().keyspace(),
            configuration().metadata(getClass()).getTable(),
            configuration().metadata(getClass()).getPrimaryKey().getName(),
            compoundKeys)).bind(params));

        if (rs.isExhausted()) {
            return Collections.emptyList();
        }

        List result = new ArrayList<>();
        for (Row row : rs.all()) {
            final Object instance = instantiate(configuration().metadata(getClass()).getEntityClass());
            configuration().fields(getClass()).stream().forEachOrdered(f -> {
                ByteBuffer value = row.getBytesUnsafe(f.getName());
                if (value != null) {
                    setField(f.getField(), instance, f.getFieldType().deserialize(value));
                }
            });
            result.add(instance);
        }
        return result;
    }

    /**
     * Find all entries in data table
     *
     * @return List of all entries in data table
     */
    @SuppressWarnings("unchecked")
    public default List<E> findAll(int limit) {
        String selectTemplate = "select * from %s.%s limit ?";
        ResultSet set = configuration().session().execute(String.format(selectTemplate, configuration().keyspace(), configuration().metadata(getClass()).getTable()), limit);
        if (set.isExhausted()) {
            return Collections.emptyList();
        }

        List result = new ArrayList<>();
        for (Row row : set.all()) {
            final Object instance = instantiate(configuration().metadata(getClass()).getEntityClass());
            configuration().fields(getClass()).stream().forEachOrdered(f -> {
                ByteBuffer value = row.getBytesUnsafe(f.getName());
                if (value != null) {
                    setField(f.getField(), instance, f.getFieldType().deserialize(value));
                }
            });
            result.add(instance);
        }
        return result;
    }

    /**
     * Save entity to database
     *
     * @param entity Entity to save
     * @throws DuplicateKeyException if there is already entity with such key in database
     */
    public default void save(E entity) throws DuplicateKeyException {
        String insertTemplate = "insert into %s.%s (%s) values (%s)";

        List<FieldMetadata> fm = configuration().fields(getClass());

        String fields = fm.stream().map(FieldMetadata::getName).collect(joining(", "));
        String values = fm.stream().map(f -> "?").collect(joining(", "));
        String str = String.format(insertTemplate, configuration().keyspace(), configuration().metadata(getClass()).getTable(), fields, values);

        log.debug("====> Inserting entity with query: {}", str);
        BoundStatement stm = boundStatement(str);
        PrimitiveIterator.OfInt counting = IntStream.range(0, configuration().metadata(getClass()).getFields().size()).iterator();

        //Get raw setValue() method of bound statement
        Method setValue = findMethod(stm.getClass(), "setValue", int.class, ByteBuffer.class);
        setValue.setAccessible(true);

        fm.stream().map(f -> new Tuple2<>(counting.next(), f)).forEachOrdered(f -> {
            Object val = readField(f.getElement2().getField(), entity);
            ByteBuffer value = null;
            if (val != null) {
                value = f.getElement2().getFieldType().serialize(val);
            }
            invoke(setValue, stm, f.getElement1(), value);
        });
        configuration().session().execute(stm);
    }

    /**
     * Delete entity from database
     *
     * @param key      Entity key
     * @param compound Entity compound keys
     */
    public default void delete(K key, Object... compound) {
        Objects.requireNonNull(key, "Key cannot be null");
        String deleteTemplate = "delete from %s.%s where %s=? %s";

        String compoundKeys = MappingUtils.compound(configuration().metadata(getClass()), compound);
        Object[] params = MappingUtils.parameters(configuration().metadata(getClass()), key, compound);

        configuration().session().execute(boundStatement(String.format(deleteTemplate,
            configuration().keyspace(),
            configuration().metadata(getClass()).getTable(),
            configuration().metadata(getClass()).getPrimaryKey().getName(),
            compoundKeys)).bind(params));

    }

}
