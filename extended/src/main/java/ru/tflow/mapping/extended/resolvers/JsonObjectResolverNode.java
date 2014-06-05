package ru.tflow.mapping.extended.resolvers;

import com.datastax.driver.core.DataType;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.extended.annotations.JsonObject;
import ru.tflow.mapping.resolvers.ChainNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 15:43
 */
public class JsonObjectResolverNode implements ChainNode {

    //Seems that ObjectMapper is thread safe
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(JsonObjectResolverNode.class);

    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        if (f.getAnnotation(JsonObject.class) != null) {
            return Optional.of(new ExtendedDataType(f.getType(), DataType.text(),
                    (o) -> {
                        try {
                            return ByteBuffer.wrap(mapper.writeValueAsBytes(o));
                        } catch (Exception e) {
                            log.error("=====> Error serializing object as json. Message: {}, Entity: {}, Field: {}",
                                    e.getMessage(), f.getDeclaringClass().getName(), f.getName());
                        }
                        return null;
                    },
                    (bb) -> {
                        byte[] bytes = new byte[bb.remaining()];
                        bb.get(bytes);
                        try {
                            return mapper.readValue(bytes, f.getType());
                        } catch (Exception e) {
                            log.error("=====> Error deserializing json text from database to object. Message: {}, Entity: {}, Field: {}\n Value:\n {}",
                                    e.getMessage(), f.getDeclaringClass().getName(), f.getName(), new String(bytes));
                        }
                        return null;
                    }));
        }

        return Optional.empty();
    }
}
