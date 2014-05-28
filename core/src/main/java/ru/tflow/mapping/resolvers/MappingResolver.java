package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;

import java.util.Optional;

/**
 * Basic interface for field type resolvers
 *
 * User: erofeev
 * Date: 11/30/13
 * Time: 9:18 PM
 */
public interface MappingResolver {

    /**
     * Try to resolve field to serializable into cassandra model data type
     *
     * @param f Field to resolve
     * @return Optional of resolved field or empty if not found
     */
    public Optional<ExtendedDataType> resolve(java.lang.reflect.Field f);

}
