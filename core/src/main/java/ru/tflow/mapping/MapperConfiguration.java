package ru.tflow.mapping;

import com.datastax.driver.core.Session;
import ru.tflow.mapping.resolvers.MappingResolver;

import java.util.List;

/**
 * Interface defining all configuration needed for mapper to operate
 * <p/>
 * Created by nagakhl on 3/24/2014.
 */
public interface MapperConfiguration {

    /**
     * Get active cassandra session
     *
     * @return Session
     */
    public Session session();

    /**
     * Get mapping resolver
     *
     * @return MappingResolver
     */
    public MappingResolver mappingResolver();

    /**
     * Get keyspace
     *
     * @return Keyspace
     */
    public String keyspace();

    /**
     * Returns if repository should fail if inconsistent type during table update found
     *
     * @return Boolean
     */
    public boolean failOnUpdate();

    /**
     * Override this to cache entity metadata in variable instead of computing new one each time
     *
     * @return EntityMetadata
     */
    public EntityMetadata metadata(Class<?> repository);

    /**
     * Get metadata fields
     *
     * @return List of FieldMetadata
     */
    default List<FieldMetadata> fields(Class<?> repository) {
        return metadata(repository).getFields();
    }

}
