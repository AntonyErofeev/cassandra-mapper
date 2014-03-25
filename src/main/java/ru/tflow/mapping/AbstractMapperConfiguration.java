package ru.tflow.mapping;

import ru.tflow.mapping.utils.MappingUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic configuration class defining
 *
 * Created by nagakhl on 3/25/2014.
 */
public abstract class AbstractMapperConfiguration implements MapperConfiguration {

    protected final Map<Class<?>, EntityMetadata> metadataCache = Collections.synchronizedMap(new HashMap<>());

    @Override
    public EntityMetadata metadata(Class<?> classOf) {
        return metadataCache.get(classOf);
    }

    @Override
    public MappingResolver mappingResolver() {
        return new DefaultMappingResolver();
    }

    public void addMetadata(CassandraRepository<?, ?> repository) {
        metadataCache.put(repository.getClass(), MappingUtils.metadata(repository.getClass(), mappingResolver()));
    }
}
