package ru.tflow.mapping.resolvers;

import com.datastax.driver.core.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.utils.Tuple3;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * Node capable of resolving additional types that can be easily enough converted into cassandra types
 *
 * Created by nagakhl on 5/27/2014.
 */
public class ExtendedResolverNode implements ChainNode {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ExtendedDataType> resolve(Field f) {
        Class<?> c = f.getType();

        //URL's
        if (URL.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.text(),
                (o) -> DataType.text().serialize(o.toString()),
                (b) -> {
                    try {
                        return new URL(DataType.text().deserialize(b).toString());
                    } catch (MalformedURLException e) {
                        log.warn("=====> Incorrect url in database: {}", DataType.text().deserialize(b).toString());
                        return null;
                    }
                }
            ));
        }

        //Enums
        if (Enum.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.text(),
                (o) -> DataType.text().serialize(((Enum) o).name()),
                (b) -> Enum.valueOf((Class<Enum>) c, DataType.text().deserialize(b).toString())));
        }


        //Java 8 date time api
        if (Instant.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.timestamp(),
                (o) -> DataType.timestamp().serialize(new Date(((Instant) o).toEpochMilli())),
                (b) -> Instant.ofEpochMilli(((Date) DataType.timestamp().deserialize(b)).getTime())));
        }

        if (LocalDateTime.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.timestamp(),
                (o) -> DataType.timestamp().serialize(new Date(((LocalDateTime) o).toInstant(ZoneOffset.UTC).toEpochMilli())),
                (b) -> LocalDateTime.ofInstant(Instant.ofEpochMilli(((Date) DataType.timestamp().deserialize(b)).getTime()), ZoneId.of("UTC"))));
        }

        if (ZonedDateTime.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.timestamp(),
                (o) -> DataType.timestamp().serialize(new Date(((ZonedDateTime) o).toInstant().toEpochMilli())),
                (b) -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(((Date) DataType.timestamp().deserialize(b)).getTime()), ZoneId.of("UTC"))));
        }

        return Optional.empty();
    }

}
