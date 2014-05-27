package ru.tflow.mapping.resolvers;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by nagakhl on 5/24/2014.
 */
public class ChainedResolver implements MappingResolver {

    private final List<ChainNode> resolverChain = new ArrayList<>();

}
