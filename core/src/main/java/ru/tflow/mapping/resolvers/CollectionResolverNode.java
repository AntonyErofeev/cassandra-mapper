package ru.tflow.mapping.resolvers;

import com.datastax.driver.core.DataType;
import org.apache.commons.lang3.reflect.TypeUtils;
import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.exceptions.CorruptedMappingException;
import ru.tflow.mapping.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Node capable of resolving collection types
 *
 * Created by nagakhl on 5/27/2014.
 */
public class CollectionResolverNode extends GenericResolver implements ChainNode {

    @Override
    public Optional<ExtendedDataType> resolve(Field f) {

        Class<?> cls = f.getType();

        if (List.class.isAssignableFrom(cls)) {
            return Optional.of(introspectList(f));
        }

        if (Set.class.isAssignableFrom(cls)) {
            return Optional.of(introspectSet(f));
        }

        if (Map.class.isAssignableFrom(cls)) {
            return Optional.of(introspectMap(f));
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    protected ExtendedDataType introspectList(Field f) throws CorruptedMappingException {

        ExtendedDataType genericType = argumentType(f, List.class).get("E");
        if (genericType == null)
            throw new CorruptedMappingException("Cannot find mapping for List type argument. Field: " + f.getName(), f.getDeclaringClass());

        return new ExtendedDataType(f.getType(), DataType.list(genericType.getMappedType()),
                lst -> {
                    if (genericType.isExtended()) {
                        List baseList = new ArrayList(((List) lst).size());
                        ((List) lst).stream().forEach(obj -> baseList.add(genericType.toMapped(obj)));
                        return DataType.list(genericType.getMappedType()).serialize(baseList);
                    }
                    return DataType.list(genericType.getMappedType()).serialize(lst);
                },
                bb -> {
                    List baseList = (List) DataType.list(genericType.getMappedType()).deserialize(bb);
                    if (genericType.isExtended()) {
                        List extended = new ArrayList(baseList.size());
                        baseList.stream().forEach(el -> extended.add(genericType.toOriginal(el)));
                        return extended;
                    }
                    return baseList;

                }
        );
    }

    @SuppressWarnings("unchecked")
    protected ExtendedDataType introspectSet(Field f) throws CorruptedMappingException {

        ExtendedDataType genericType = argumentType(f, Set.class).get("E");
        if (genericType == null)
            throw new CorruptedMappingException("Cannot find mapping for Set type argument. Field: " + f.getName(), f.getDeclaringClass());

        return new ExtendedDataType(f.getType(), DataType.set(genericType.getMappedType()),
                set -> {
                    if (genericType.isExtended()) {
                        Set baseSet = new HashSet();
                        ((Set) set).stream().forEach(obj -> baseSet.add(genericType.toMapped(obj)));
                        return DataType.set(genericType.getMappedType()).serialize(baseSet);
                    }
                    return DataType.set(genericType.getMappedType()).serialize(set);
                },
                buf -> {
                    Set baseSet = (Set) DataType.set(genericType.getMappedType()).deserialize(buf);
                    if (genericType.isExtended()) {
                        Set extended = new HashSet();
                        baseSet.stream().forEach(el -> extended.add(genericType.toOriginal(el)));
                        return extended;
                    }
                    return baseSet;
                }
        );

    }

    @SuppressWarnings("unchecked")
    protected ExtendedDataType introspectMap(Field f) throws CorruptedMappingException {
        Class<?> cls = f.getType();

        Map<String, ExtendedDataType> argumentTypes = argumentType(f, Map.class);

        final ExtendedDataType fKey = argumentTypes.get("K");
        final ExtendedDataType fVal = argumentTypes.get("V");

        DataType mappedType = DataType.map(fKey.getMappedType(), fVal.getMappedType());
        return new ExtendedDataType(cls, mappedType,
                map -> {
                    Map baseMap;
                    if (fKey.isExtended() || fVal.isExtended()) {
                        baseMap = new HashMap();
                        ((Map<Object, Object>) map).entrySet().stream().forEach(el
                                -> baseMap.put(fKey.toMapped(el.getKey()), fVal.toMapped(el.getValue())));
                    } else {
                        baseMap = (Map) map;
                    }
                    return mappedType.serialize(baseMap);
                },
                bb -> {
                    Map<Object, Object> baseMap = (Map) DataType.map(mappedType.getTypeArguments().get(0), mappedType.getTypeArguments().get(1)).deserialize(bb);
                    if (fKey.isExtended() || fVal.isExtended()) {
                        Map extendedMap = new HashMap();
                        baseMap.entrySet().stream().forEach(el
                                -> extendedMap.put(fKey.toOriginal(el.getKey()), fVal.toOriginal(el.getValue())));
                        return extendedMap;
                    }
                    return baseMap;
                }
        );
    }
}
