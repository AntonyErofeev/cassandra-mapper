package ru.tflow.mapping.resolvers;

import com.datastax.driver.core.DataType;
import ru.tflow.mapping.ExtendedDataType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * Node capable of resolving and mapping types that can be directly translated into cassandra types
 *
 * Created by nagakhl on 5/27/2014.
 */
public class DirectResolverNode implements ChainNode, ClassResolver {

    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        return resolve(f.getType());
    }

    @Override
    public Optional<ExtendedDataType> resolve(Class<?> c) {
        //String
        if (String.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.text()));

        //Net addresses
        if (InetAddress.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.inet()));

        //Numbers
        if (BigDecimal.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.decimal()));
        if (BigInteger.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.bigint()));
        if (Short.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.cint()));
        if (Integer.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.cint()));
        if (Long.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.bigint()));
        if (Character.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.cint()));
        if (Float.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.cfloat()));
        if (Double.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.cdouble()));
        if (Boolean.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.cboolean()));
        if (Byte.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.cint()));

        //Date
        if (Date.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.timestamp()));

        //UUID
        if (UUID.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.uuid()));

        //Binary
        if (ByteBuffer.class.isAssignableFrom(c)) return Optional.of(new ExtendedDataType(c, DataType.blob()));

        return Optional.empty();
    }
}
