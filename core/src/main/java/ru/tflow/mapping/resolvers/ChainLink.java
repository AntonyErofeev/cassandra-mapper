package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * One element of resolver chain.
 * <p/>
 * Created by nagakhl on 5/25/2014.
 */
public interface ChainLink {

    public Optional<ExtendedDataType> resolve(Field f);

}
