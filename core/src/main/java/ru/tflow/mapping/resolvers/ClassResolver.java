package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;

import java.util.Optional;

/**
 * Additional interfaces for resolver
 *
 * User: nagakhl
 * Date: 28.05.2014
 * Time: 15:23
 */
public interface ClassResolver {

    public Optional<ExtendedDataType> resolve(Class<?> cls);

}
