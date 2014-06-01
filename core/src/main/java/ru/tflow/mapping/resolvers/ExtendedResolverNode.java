package ru.tflow.mapping.resolvers;

import com.datastax.driver.core.DataType;
import ru.tflow.mapping.ExtendedDataType;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * Node capable of resolving additional types that can be easily enough converted into cassandra types
 *
 * Created by nagakhl on 5/27/2014.
 */
public class ExtendedResolverNode implements ChainNode, ClassResolver {

    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        return resolve(f.getType());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<ExtendedDataType> resolve(Class<?> c) {
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
            return Optional.of(new ExtendedDataType(c, DataType.ascii(),
                    (o) -> DataType.ascii().serialize(((LocalDateTime) o).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)),
                    (b) -> LocalDateTime.parse((String) DataType.ascii().deserialize(b))));
        }

        if (ZonedDateTime.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.ascii(),
                    (o) -> DataType.ascii().serialize(((ZonedDateTime) o).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)),
                    (b) -> ZonedDateTime.parse((String) DataType.ascii().deserialize(b))));
        }

        return Optional.empty();
    }
}
