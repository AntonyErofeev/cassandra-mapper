package ru.tflow.mapping.resolvers;

import com.datastax.driver.core.DataType;
import ru.tflow.mapping.ExtendedDataType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Node capable of resolving and mapping primitives
 *
 * Created by nagakhl on 5/26/2014.
 */
public class PrimitiveResolverNode implements ChainNode {

    private static final Map<Class<?>, DataType> primitivesMap = new HashMap<>();
    
    static {
        primitivesMap.put(Boolean.TYPE, DataType.cboolean());
        primitivesMap.put(Byte.TYPE, DataType.cint());
        primitivesMap.put(Character.TYPE, DataType.cint());
        primitivesMap.put(Short.TYPE, DataType.cint());
        primitivesMap.put(Integer.TYPE, DataType.cint());
        primitivesMap.put(Long.TYPE, DataType.bigint());
        primitivesMap.put(Double.TYPE, DataType.cdouble());
        primitivesMap.put(Float.TYPE, DataType.cfloat());
    }
    
    @Override
    public Optional<ExtendedDataType> resolve(Field f) {
        //If field is not primitive just return
        if (!f.getType().isPrimitive()) return Optional.empty();
        
        return Optional.of(new ExtendedDataType(f.getType(), primitivesMap.get(f.getType())));

    }
}
