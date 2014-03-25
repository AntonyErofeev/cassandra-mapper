package ru.tflow.mapping.utils;

import org.apache.commons.lang3.reflect.TypeUtils;
import ru.tflow.mapping.CassandraRepository;
import ru.tflow.mapping.EntityMetadata;
import ru.tflow.mapping.FieldMetadata;
import ru.tflow.mapping.MappingResolver;
import ru.tflow.mapping.exceptions.CorruptedMappingException;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by erofeev on 2/17/14.
 */
public class MappingUtils {

    /**
     * Create query parameter array from primary and compound keys
     */
    public static Object[] parameters(Object key, Object... compound) {
        Objects.requireNonNull(key, "Key cannot be null");
        Object[] params = new Object[1 + compound.length];
        params[0] = key;
        System.arraycopy(compound, 0, params, 1, compound.length);
        return params;
    }

    /**
     * Format field type for use in CQL
     *
     * @param f Field metadata
     * @return field type string
     */
    public static String formatFieldType(FieldMetadata f) {
        if (f.getFieldType().getMappedType().isCollection()) {
            return f.getFieldType().getMappedType().getName().toString() +
                    f.getFieldType().getMappedType().getTypeArguments()
                            .stream()
                            .map(t -> t.getName().toString())
                            .collect(Collectors.joining(", ", "<", ">"));
        }
        return f.getFieldType().getMappedType().getName().toString();
    }

    /**
     * Create query part for compound keys
     */
    public static String compound(EntityMetadata metadata, Object... compound) {
        String compoundKeys = "";
        if (compound != null && compound.length > 0) {
            StringBuilder compoundTemplate = new StringBuilder();
            for (FieldMetadata fm : metadata.getKeys()) {
                compoundTemplate.append(" and ").append(fm.getName()).append("=?");
            }
            compoundKeys = compoundTemplate.toString();
        }
        return compoundKeys;
    }

    public static EntityMetadata metadata(Class<?> classOf, MappingResolver resolver) {
        Class cls = (Class) TypeUtils.getTypeArguments(classOf, CassandraRepository.class)
            .entrySet()
            .stream()
            .filter(e -> e.getKey().getGenericDeclaration().equals(CassandraRepository.class))
            .filter(e -> e.getKey().getName().equals("E"))
            .findFirst()
            .orElseThrow(() -> new CorruptedMappingException("Cannot find generic declaration of repository.", classOf))
            .getValue();

        return new EntityMetadata(cls, resolver);
    }
}
