package ru.tflow.mapping.resolvers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tflow.mapping.ExtendedDataType;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * One element of resolver chain.
 * <p/>
 * Created by nagakhl on 5/25/2014.
 */
public interface ChainNode {

    static Logger log = LoggerFactory.getLogger(ChainNode.class);

    public Optional<ExtendedDataType> resolve(Field f);

}
