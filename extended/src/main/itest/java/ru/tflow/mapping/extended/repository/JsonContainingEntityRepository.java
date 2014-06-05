package ru.tflow.mapping.extended.repository;

import ru.tflow.mapping.DatatableManager;
import ru.tflow.mapping.MapperConfiguration;
import ru.tflow.mapping.extended.entity.JsonContainingEntity;

import java.util.UUID;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 16:32
 */
public class JsonContainingEntityRepository implements DatatableManager<JsonContainingEntity, UUID> {

    public JsonContainingEntityRepository() {
        ConfigurationHolder.configuration().addMetadata(this);
    }

    @Override
    public MapperConfiguration conf() {
        return ConfigurationHolder.configuration();
    }
}
