package ru.tflow.mapping.resolvers;

import com.datastax.driver.core.DataType;
import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.utils.Tuple3;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;

/**
 * Node capable of resolving and mapping primitives
 *
 * Created by nagakhl on 5/26/2014.
 */
public class PrimitiveResolverNode implements ChainNode, ClassResolver {

    private static final Map<Class<?>, Tuple3<DataType, Function<Object, ByteBuffer>, Function<ByteBuffer, Object>>> primitivesMap = new HashMap<>();
    
    static {
        primitivesMap.put(Boolean.TYPE, new Tuple3<>(DataType.cboolean(), (b) -> (ByteBuffer) wrap(new byte[]{1}).flip(), (buf) -> buf.get(0) != 0));
        primitivesMap.put(Byte.TYPE, new Tuple3<>(DataType.cint(), (b) -> (ByteBuffer) allocate(4).put((byte) b).flip(), (b) -> b.get(0)));
        primitivesMap.put(Character.TYPE, new Tuple3<>(DataType.cint(), (b) -> (ByteBuffer) allocate(4).putChar((char) b).flip(), ByteBuffer::getChar));
        primitivesMap.put(Short.TYPE, new Tuple3<>(DataType.cint(), (b) -> (ByteBuffer) allocate(4).putShort((short) b).flip(), ByteBuffer::getShort));
        primitivesMap.put(Integer.TYPE, new Tuple3<>(DataType.cint(), (b) -> (ByteBuffer) allocate(4).putInt((int) b).flip(), ByteBuffer::getInt));
        primitivesMap.put(Long.TYPE, new Tuple3<>(DataType.bigint(), (b) -> (ByteBuffer) allocate(8).putLong((long) b).flip(), ByteBuffer::getLong));
        primitivesMap.put(Double.TYPE, new Tuple3<>(DataType.cdouble(), (b) -> (ByteBuffer) allocate(8).putDouble((double) b).flip(), ByteBuffer::getDouble));
        primitivesMap.put(Float.TYPE, new Tuple3<>(DataType.cfloat(), (b) -> (ByteBuffer) allocate(4).putFloat((float) b).flip(), ByteBuffer::getFloat));
    }
    
    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        return resolve(f.getType());
    }

    @Override
    public Optional<ExtendedDataType> resolve(Class<?> cls) {
        //If field is not primitive just return
        if (!cls.isPrimitive()) return Optional.empty();

        if (primitivesMap.containsKey(cls)) {
            Tuple3<DataType, Function<Object, ByteBuffer>, Function<ByteBuffer, Object>> mapping = primitivesMap.get(cls);
            return Optional.of(new ExtendedDataType(cls, mapping.getElement1(), mapping.getElement2(), mapping.getElement3()));
        }

        return Optional.empty();
    }
}
