package ru.tflow.mapping.repository;

import ru.tflow.mapping.CassandraRepository;
import ru.tflow.mapping.DatatableManager;
import ru.tflow.mapping.MapperConfiguration;
import ru.tflow.mapping.entity.SimpleEntity;

import java.util.UUID;

/**
 *
 * Created by nagakhl on 3/25/2014.
 */
public class AdvancedSimpleEntityRepository implements DatatableManager<SimpleEntity.AdvancedSimpleEntity, UUID> {

    public AdvancedSimpleEntityRepository() {
        ConfigurationHolder.configuration().addMetadata(this);
    }

    @Override
    public MapperConfiguration configuration() {
        return ConfigurationHolder.configuration();
    }
}
