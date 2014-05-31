package ru.tflow.mapping.repository;

import ru.tflow.mapping.DatatableManager;
import ru.tflow.mapping.MapperConfiguration;
import ru.tflow.mapping.entity.CompoundKeyEntity;

import java.util.UUID;

/**
 *
 * Created by nagakhl on 3/28/2014.
 */
public class CompoundRepository implements DatatableManager<CompoundKeyEntity, UUID> {

    public CompoundRepository() {
        ConfigurationHolder.configuration().addMetadata(this);
    }

    @Override
    public MapperConfiguration conf() {
        return ConfigurationHolder.configuration();
    }
}
