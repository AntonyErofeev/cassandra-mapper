package ru.tflow.mapping;

import com.datastax.driver.core.DataType;
import org.apache.commons.lang3.ClassUtils;
import ru.tflow.mapping.exceptions.CorruptedMappingException;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

/**
 * User: erofeev
 * Date: 12/3/13
 * Time: 10:20 PM
 */
public class ExtendedDataType {

    private DataType mappedType;

    private Class<?> originalType;

    private Function<Object, ByteBuffer> serialize;

    private Function<ByteBuffer, Object> deserialize;

    public ExtendedDataType(Class type,
                            DataType mappedType) {

        if (type.isPrimitive()) {
            type = ClassUtils.primitiveToWrapper(type);
        }

        this.originalType = Objects.requireNonNull(type, "Class cannot be null.");
        this.mappedType = Objects.requireNonNull(mappedType, "Mapped type cannot be null");

        if (!mappedType.asJavaClass().isAssignableFrom(type)) {
            throw new CorruptedMappingException("Mapped type: " + mappedType.getName().asJavaClass().getSimpleName() + " is not assignable from field type: " + type.getSimpleName()
                + ", and no serialize function given.", type.getDeclaringClass());
        }

        this.serialize = mappedType::serialize;
        this.deserialize = mappedType::deserialize;
    }

    public ExtendedDataType(Class type,
                            DataType mappedType,
                            Function<Object, ByteBuffer> serialize,
                            Function<ByteBuffer, Object> deserialize) {

        this.originalType = Objects.requireNonNull(type, "Class cannot be null.");
        this.mappedType = Objects.requireNonNull(mappedType, "Mapped type cannot be null");

        this.serialize = serialize;
        this.deserialize = deserialize;
    }

    public DataType getMappedType() {
        return mappedType;
    }

    public Class<?> getOriginalType() {
        return originalType;
    }

    public boolean isExtended() {
        return !mappedType.getName().asJavaClass().isAssignableFrom(getOriginalType());
    }

    public ByteBuffer serialize(Object o) {
        return serialize.apply(o);
    }

    public Object deserialize(ByteBuffer b) {
        return deserialize.apply(b);
    }

    public Object toMapped(Object o) {
        return isExtended() ? mappedType.deserialize(serialize.apply(o)) : o;
    }

    public Object toOriginal(Object o) {
        return isExtended() ? deserialize.apply(mappedType.serialize(o)) : o;
    }

}
