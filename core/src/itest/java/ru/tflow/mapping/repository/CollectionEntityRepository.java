package ru.tflow.mapping.repository;

import ru.tflow.mapping.DatatableManager;
import ru.tflow.mapping.MapperConfiguration;
import ru.tflow.mapping.entity.CollectionContainingEntity;

import java.util.UUID;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 13:12
 */
public class CollectionEntityRepository implements DatatableManager<CollectionContainingEntity, UUID> {

    public CollectionEntityRepository() {
        ConfigurationHolder.configuration().addMetadata(this);
    }

    @Override
    public MapperConfiguration conf() {
        return ConfigurationHolder.configuration();
    }
}
