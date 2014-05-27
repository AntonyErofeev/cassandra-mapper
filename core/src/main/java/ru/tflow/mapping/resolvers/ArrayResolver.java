package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;

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
public class ArrayResolver implements ChainNode {

    private final List<ChainNode> genericsResolveChain = new ArrayList<>();

    public ArrayResolver() {
        genericsResolveChain.add(new DirectResolverNode());
        genericsResolveChain.add(new ExtendedResolverNode());
    }

    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        if (!f.getType().isArray()) return Optional.empty();

        return null;
    }
}
