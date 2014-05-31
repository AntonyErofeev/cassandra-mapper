package ru.tflow.mapping.repository;

import ru.tflow.mapping.DatatableManager;
import ru.tflow.mapping.MapperConfiguration;
import ru.tflow.mapping.entity.SimpleEntity;

import java.util.UUID;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class SimpleEntityRepository implements DatatableManager<SimpleEntity, UUID> {

    public SimpleEntityRepository() {
        ConfigurationHolder.configuration().addMetadata(this);
    }

    @Override
    public MapperConfiguration conf() {
        return ConfigurationHolder.configuration();
    }
}
