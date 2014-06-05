package ru.tflow.mapping.extended;

import ru.tflow.mapping.extended.resolvers.JsonObjectResolverNode;
import ru.tflow.mapping.resolvers.ChainedResolver;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 16:25
 */
public class ExtendedChainResolver extends ChainedResolver {

    public ExtendedChainResolver() {
        super();
        getResolverChain().add(0, new JsonObjectResolverNode());
    }
}
