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
        Class<?> cls = f.getType();
        Optional<Map.Entry<TypeVariable<?>, Type>> o = ReflectionUtils.findType(f, List.class);
        if (o.isPresent()) {
            Optional<ExtendedDataType> optType = resolveWithExtended((Class<?>) o.get().getValue());
            if (optType.isPresent()) {
                DataType dataType = optType.get().getMappedType();
                return new ExtendedDataType(cls, DataType.list(dataType),
                        lst -> {
                            if (optType.get().isExtended()) {
                                List baseList = new ArrayList(((List) lst).size());
                                ((List) lst).stream().forEach(obj -> baseList.add(optType.get().toMapped(obj)));
                                return DataType.list(dataType).serialize(baseList);
                            }
                            return DataType.list(dataType).serialize(lst);
                        },
                        bb -> {
                            List baseList = (List) DataType.list(dataType).deserialize(bb);
                            if (optType.get().isExtended()) {
                                List extended = new ArrayList(baseList.size());
                                baseList.stream().forEach(el -> extended.add(optType.get().toOriginal(el)));
                                return extended;
                            }
                            return baseList;

                        }
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected ExtendedDataType introspectSet(Field f) throws CorruptedMappingException {
        Class<?> cls = f.getType();
        Optional<Map.Entry<TypeVariable<?>, Type>> o = ReflectionUtils.findType(f, Set.class);
        if (o.isPresent()) {
            Optional<ExtendedDataType> optType = resolveWithExtended((Class<?>) o.get().getValue());
            if (optType.isPresent()) {
                DataType dt = optType.get().getMappedType();
                return new ExtendedDataType(cls, DataType.set(dt),
                        set -> {
                            if (optType.get().isExtended()) {
                                Set baseSet = new HashSet();
                                ((Set) set).stream().forEach(obj -> baseSet.add(optType.get().toMapped(obj)));
                                return DataType.set(dt).serialize(baseSet);
                            }
                            return DataType.set(dt).serialize(set);
                        },
                        buf -> {
                            Set baseSet = (Set) DataType.set(dt).deserialize(buf);
                            if (optType.get().isExtended()) {
                                Set extended = new HashSet();
                                baseSet.stream().forEach(el -> extended.add(optType.get().toOriginal(el)));
                                return extended;
                            }
                            return baseSet;
                        }
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected ExtendedDataType introspectMap(Field f) throws CorruptedMappingException {
        Class<?> cls = f.getType();
        //Get all type variables
        Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments(f.getGenericType(), Map.class);

        //Filter not related to map interface
        Set<HashMap.Entry<TypeVariable<?>, Type>> entries = args.entrySet().stream()
                .filter(v -> v.getKey().getGenericDeclaration().equals(Map.class)).collect(Collectors.toSet());

        Optional<ExtendedDataType> key = Optional.empty();
        Optional<ExtendedDataType> val = Optional.empty();

        for (HashMap.Entry<TypeVariable<?>, Type> e : entries) {
            if (e.getKey().getName().equals("K")) key = resolveWithExtended((Class) e.getValue());
            if (e.getKey().getName().equals("V")) val = resolveWithExtended((Class) e.getValue());
        }

        final Optional<ExtendedDataType> fKey = key;
        final Optional<ExtendedDataType> fVal = val;

        if (key.isPresent() && val.isPresent()) {
            DataType mappedType = DataType.map(key.get().getMappedType(), val.get().getMappedType());
            return new ExtendedDataType(cls, mappedType,
                    map -> {
                        Map baseMap;
                        if (fKey.get().isExtended() || fVal.get().isExtended()) {
                            baseMap = new HashMap();
                            ((Map<Object, Object>) map).entrySet().stream().forEach(el
                                    -> baseMap.put(fKey.get().toMapped(el.getKey()), fVal.get().toMapped(el.getValue())));
                        } else {
                            baseMap = (Map) map;
                        }
                        return mappedType.serialize(baseMap);
                    },
                    bb -> {
                        Map<Object, Object> baseMap = (Map) DataType.map(mappedType.getTypeArguments().get(0), mappedType.getTypeArguments().get(1)).deserialize(bb);
                        if (fKey.get().isExtended() || fVal.get().isExtended()) {
                            Map extendedMap = new HashMap();
                            baseMap.entrySet().stream().forEach(el
                                    -> extendedMap.put(fKey.get().toOriginal(el.getKey()), fVal.get().toOriginal(el.getValue())));
                            return extendedMap;
                        }
                        return baseMap;
                    }
            );
        }
    }
}
