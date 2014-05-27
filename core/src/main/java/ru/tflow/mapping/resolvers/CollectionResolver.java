package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Node capable of resolving collection types
 *
 * Created by nagakhl on 5/27/2014.
 */
public class CollectionResolver implements ChainNode {
    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        return null;
    }
}
