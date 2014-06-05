package ru.tflow.mapping.extended;

import ru.tflow.mapping.AbstractMapperConfiguration;
import ru.tflow.mapping.resolvers.MappingResolver;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 16:25
 */
public abstract class ExtendedMapperConfiguration extends AbstractMapperConfiguration {

    @Override
    public MappingResolver mappingResolver() {
        return new ExtendedChainResolver();
    }
}
