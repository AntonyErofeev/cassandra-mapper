package ru.tflow.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for configuration provider
 *
 * Created by nagakhl on 3/24/2014.
 */
public interface MapperConfigurationProvider {

    static final Logger log = LoggerFactory.getLogger(CassandraRepository.class);

    public MapperConfiguration configuration();

}
