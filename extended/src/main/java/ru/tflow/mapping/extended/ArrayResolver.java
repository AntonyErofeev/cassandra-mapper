package ru.tflow.mapping.extended;

import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.resolvers.ChainNode;
import ru.tflow.mapping.resolvers.GenericResolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Node capable of resolving arrays. All arrays must be of primitive types or of types that can be directly mapped into cassandra types.
 *
 * Arrays are mapped as cassandra List&lt;T&gt; type.
 *
 * User: nagakhl
 * Date: 27.05.2014
 * Time: 13:46
 */
public class ArrayResolver extends GenericResolver implements ChainNode {

    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        if (!f.getType().isArray()) return Optional.empty();

        return null;
    }
}
