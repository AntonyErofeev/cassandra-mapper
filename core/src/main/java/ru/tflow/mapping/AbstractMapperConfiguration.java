package ru.tflow.mapping;

import com.datastax.driver.core.PreparedStatement;
import ru.tflow.mapping.resolvers.ChainedResolver;
import ru.tflow.mapping.resolvers.MappingResolver;
import ru.tflow.mapping.utils.MappingUtils;
import ru.tflow.mapping.utils.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Basic configuration class defining
 * <p/>
 * Created by nagakhl on 3/25/2014.
 */
public abstract class AbstractMapperConfiguration implements MapperConfiguration {

    protected final Map<Class<?>, EntityMetadata> metadataCache = Collections.synchronizedMap(new HashMap<>());

    protected final Map<Tuple2<Class<?>, String>, PreparedStatement> queryCache = Collections.synchronizedMap(new HashMap<>());

    @Override
    public EntityMetadata metadata(Class<?> classOf) {
        return metadataCache.get(classOf);
    }

    @Override
    public MappingResolver mappingResolver() {
        return new ChainedResolver();
    }

    public void addMetadata(CassandraRepository<?, ?> repository) {
        metadataCache.put(repository.getClass(), MappingUtils.metadata(repository.getClass(), mappingResolver()));
    }

    @Override
    public Optional<PreparedStatement> getStatement(Class<?> mappedObject, String queryType) {
        return Optional.ofNullable(queryCache.get(new Tuple2<Class<?>, String>(mappedObject, queryType)));
    }

    @Override
    public void putStatement(Class<?> mappedObject, String queryType, PreparedStatement stm) {
        queryCache.put(new Tuple2<>(mappedObject, queryType), stm);
    }
}
