package ru.tflow.mapping.utils;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import org.apache.commons.lang3.reflect.TypeUtils;
import ru.tflow.mapping.CassandraRepository;
import ru.tflow.mapping.EntityMetadata;
import ru.tflow.mapping.FieldMetadata;
import ru.tflow.mapping.resolvers.MappingResolver;
import ru.tflow.mapping.exceptions.CorruptedMappingException;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Static helper methods.
 *
 * Created by nagakhl on 2/17/14.
 */
public class MappingUtils {

    /**
     * Create query parameter array from primary and compound keys
     */
    public static ByteBuffer[] parameters(EntityMetadata metadata, Object key, Object... compound) {
        Objects.requireNonNull(key, "Key cannot be null");
        ByteBuffer[] params = new ByteBuffer[1 + compound.length];
        params[0] = metadata.getPrimaryKey().getFieldType().serialize(key);
        for (int i = 0; i < compound.length; i++) {
            params[i + 1] = metadata.getKeys().get(i).getFieldType().serialize(compound[i]);
        }
        return params;
    }

    /**
     * Bind primary and compound keys to given prepared statement
     *
     * @param stm PreparedStatement
     * @param em EntityMetadata
     * @param key Primary key
     * @param compound Compound keys in order from @Compound annotations
     * @return BoundStatement with needed parameters bound
     */
    public static BoundStatement bindKeys(PreparedStatement stm, EntityMetadata em, Object key, Object... compound) {
        ByteBuffer[] parameters = parameters(em, key, compound);
        BoundStatement bStm = stm.bind();
        Method setValueMethod = ReflectionUtils.findMethod(BoundStatement.class, "setValue", Integer.TYPE, ByteBuffer.class);
        setValueMethod.setAccessible(true);
        ReflectionUtils.invoke(setValueMethod, bStm, 0, parameters[0]);
        for (int i = 1; i < parameters.length; i++) {
            ReflectionUtils.invoke(setValueMethod, bStm, i, parameters[i]);
        }
        return bStm;
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
