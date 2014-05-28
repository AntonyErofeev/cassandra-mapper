package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * Created by nagakhl on 5/24/2014.
 */
public class ChainedResolver implements MappingResolver {

    private final List<ChainNode> resolverChain = new ArrayList<>();

    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        return null;
    }
}
