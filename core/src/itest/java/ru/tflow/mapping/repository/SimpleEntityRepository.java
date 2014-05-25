package ru.tflow.mapping.repository;

import com.datastax.driver.core.Session;
import ru.tflow.mapping.*;
import ru.tflow.mapping.entity.SimpleEntity;

import java.util.UUID;

/**
 * Created by nagakhl on 3/25/2014.
 */
public class SimpleEntityRepository implements DatatableManager<SimpleEntity, UUID> {

    public SimpleEntityRepository() {
        ConfigurationHolder.configuration().addMetadata(this);
    }

    @Override
    public MapperConfiguration configuration() {
        return ConfigurationHolder.configuration();
    }
}
