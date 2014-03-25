package ru.tflow.mapping;

import com.datastax.driver.core.DataType;
import org.apache.commons.lang3.reflect.TypeUtils;
import ru.tflow.mapping.utils.Tuple3;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: erofeev
 * Date: 11/30/13
 * Time: 9:18 PM
 */
public interface MappingResolver {

    public default Map<Class<?>, Tuple3<DataType, Function<Object, ByteBuffer>, Function<ByteBuffer, Object>>> extendedSerializers() {
        return Collections.emptyMap();
    }

    /**
     * Try to resolve field to serializable into cassandra model data type
     *
     * @param f Field to resolve
     * @return Optional of resolved field or empty if not found
     */
    public default Optional<ExtendedDataType> resolve(java.lang.reflect.Field f) {

        //Check if type is a collection
        Optional<ExtendedDataType> type = resolveWithCollections(f);
        if (type.isPresent())
            return type;

        return Optional.empty();
    }

    /**
     * Try to resolve field to one of internally supported by cassandra types including collection types
     *
     * @param f Type of fField to resolve
     * @return Optional of extended data type
     */
    @SuppressWarnings("unchecked")
    default Optional<ExtendedDataType> resolveWithCollections(java.lang.reflect.Field f) {

        Class<?> cls = f.getType();

        Optional<ExtendedDataType> type = resolveWithExtended(cls);
        if (type.isPresent()) return type;

        if (List.class.isAssignableFrom(cls)) {
            Optional<Map.Entry<TypeVariable<?>, Type>> o = findType(f, List.class);
            if (o.isPresent()) {
                Optional<ExtendedDataType> optType = resolveWithExtended((Class<?>) o.get().getValue());
                if (optType.isPresent()) {
                    DataType dataType = optType.get().getMappedType();
                    return Optional.of(new ExtendedDataType(cls, DataType.list(dataType),
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

                            }));
                }
            }
        }
        if (Set.class.isAssignableFrom(cls)) {
            Optional<Map.Entry<TypeVariable<?>, Type>> o = findType(f, Set.class);
            if (o.isPresent()) {
                Optional<ExtendedDataType> optType = resolveWithExtended((Class<?>) o.get().getValue());
                if (optType.isPresent()) {
                    DataType dt = optType.get().getMappedType();
                    return Optional.of(new ExtendedDataType(cls, DataType.set(dt),
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
                            }));
                }
            }
        }

        if (Map.class.isAssignableFrom(cls)) {
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
                return Optional.of(new ExtendedDataType(cls, mappedType,
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
                        }));
            }
        }

        return Optional.empty();

    }

    default Optional<Map.Entry<TypeVariable<?>, Type>> findType(java.lang.reflect.Field f, Class<?> cls) {
        Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments(f.getGenericType(), cls);
        return args.entrySet()
                .stream()
                .filter(v -> v.getKey().getGenericDeclaration().equals(cls))
                .findFirst();
    }

    /**
     * Try to resolve elementary type and if not found - try extended builtin types
     *
     * @param c Class of field to resolve
     * @return Optional of resolved field or empty if not found
     */
    @SuppressWarnings("unchecked")
    default Optional<ExtendedDataType> resolveWithExtended(Class<?> c) {
        Optional<DataType> base = resolveBase(c);
        if (base.isPresent()) return Optional.of(new ExtendedDataType(c, base.get()));

        if (URL.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.text(),
                    (o) -> DataType.text().serialize(o.toString()),
                    (b) -> {
                        try {
                            return new URL(DataType.text().deserialize(b).toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }));
        }

        if (Enum.class.isAssignableFrom(c)) {
            return Optional.of(new ExtendedDataType(c, DataType.text(),
                    (o) -> DataType.text().serialize(((Enum) o).name()),
                    (b) -> Enum.valueOf((Class<Enum>) c, DataType.text().deserialize(b).toString())));
        }

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

        //Check if we have user defined serializer for this type
        if (extendedSerializers().containsKey(c)) {
            Tuple3<DataType, Function<Object, ByteBuffer>, Function<ByteBuffer, Object>> t = extendedSerializers().get(c);
            return Optional.of(new ExtendedDataType(c, t.getElement1(), t.getElement2(), t.getElement3()));
        }

        return Optional.empty();
    }

    /**
     * Resolve elementary type to one of cassandra types
     *
     * @param c Class to resolve
     * @return Optional of resolved type
     */
    default Optional<DataType> resolveBase(Class<?> c) {
        //String
        if (String.class.isAssignableFrom(c)) return Optional.of(DataType.text());

        //Net addresses
        if (InetAddress.class.isAssignableFrom(c)) return Optional.of(DataType.inet());

        //Numbers
        if (BigDecimal.class.isAssignableFrom(c)) return Optional.of(DataType.decimal());
        if (Integer.class.isAssignableFrom(c)) return Optional.of(DataType.cint());
        if (Long.class.isAssignableFrom(c)) return Optional.of(DataType.bigint());
        if (Float.class.isAssignableFrom(c)) return Optional.of(DataType.cfloat());
        if (Double.class.isAssignableFrom(c)) return Optional.of(DataType.cdouble());
        if (Boolean.class.isAssignableFrom(c)) return Optional.of(DataType.cboolean());

        //Date
        if (Date.class.isAssignableFrom(c)) return Optional.of(DataType.timestamp());

        //UUID
        if (UUID.class.isAssignableFrom(c)) return Optional.of(DataType.uuid());

        //Binary
        if (ByteBuffer.class.isAssignableFrom(c)) return Optional.of(DataType.blob());

        return Optional.empty();
    }

}
