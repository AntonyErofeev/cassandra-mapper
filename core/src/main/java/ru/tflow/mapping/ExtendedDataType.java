package ru.tflow.mapping;

import com.datastax.driver.core.DataType;
import org.apache.commons.lang3.ClassUtils;
import ru.tflow.mapping.exceptions.CorruptedMappingException;
import ru.tflow.mapping.utils.ReflectionUtils;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.Function;

/**
 * Class holding extended data about type of mapped field.
 *
 * User: erofeev
 * Date: 12/3/13
 * Time: 10:20 PM
 */
public class ExtendedDataType {

    /**
     * Type of field in cassandra database
     */
    private final DataType mappedType;

    /**
     * Original type of field in mapped class
     */
    private final Class<?> originalType;

    /**
     * Function that serializes original type to ByteBuffer
     */
    private final Function<Object, ByteBuffer> serialize;

    /**
     * Function that deserializes from ByteBuffer to original type
     */
    private final Function<ByteBuffer, Object> deserialize;

    /**
     * Creates ExtendedDataType instance with default serialize/deserialize functions provided by cassandra driver.
     * <p/>
     * Should be used only if type can be directly mapped to cassandra type
     *
     * @param originalType Type of variable in class
     * @param mappedType   Type this variable should be mapped to
     * @throws ru.tflow.mapping.exceptions.CorruptedMappingException if mapped type cannot be directly converted to original type
     */
    public ExtendedDataType(Class originalType,
                            DataType mappedType) {

        this.originalType = Objects.requireNonNull(originalType, "Class cannot be null.");
        this.mappedType = Objects.requireNonNull(mappedType, "Mapped type cannot be null");

        if (!mappedType.asJavaClass().isAssignableFrom(originalType)) {
            throw new CorruptedMappingException("Mapped type: " + mappedType.getName().asJavaClass().getSimpleName()
                + " is not assignable from field type: " + originalType.getSimpleName()
                + ", and no serialize function given.", originalType.getDeclaringClass());
        }

        this.serialize = mappedType::serialize;
        this.deserialize = mappedType::deserialize;
    }

    /**
     * Construct ExtendedDataType using provided serialize|deserialize functions
     *
     * @param originalType Type of variable in class
     * @param mappedType   Type this variable should be mapped to
     * @param serialize    Function that serializes original type to ByteBuffer
     * @param deserialize  Function that deserializes original type from ByteBuffer
     */
    public ExtendedDataType(Class originalType,
                            DataType mappedType,
                            Function<Object, ByteBuffer> serialize,
                            Function<ByteBuffer, Object> deserialize) {

        this.originalType = Objects.requireNonNull(originalType, "Class cannot be null.");
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
