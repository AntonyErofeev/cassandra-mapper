package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;

import java.util.Optional;

/**
 * Additional interfaces for resolver chain nodes. Declares method for resolving type to cassandra type based only on Class of field,
 * not it's Field object. Limitation is that ClassResolver cannot resolve generic parameters of field if such exist.
 *
 * User: nagakhl
 * Date: 28.05.2014
 * Time: 15:23
 */
public interface ClassResolver {

    /**
     * Resolve Class to cassandra type. Not applicable to generified types.
     *
     * @param cls Class
     * @return Optional of ExtendedDataType
     */
    public Optional<ExtendedDataType> resolve(Class<?> cls);

}
