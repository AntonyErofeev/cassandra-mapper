package ru.tflow.mapping.resolvers;

import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.exceptions.CorruptedMappingException;
import ru.tflow.mapping.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base class for resolvers that need not only resolve type but also it's type arguments.
 *
 * User: nagakhl
 * Date: 27.05.2014
 * Time: 18:08
 */
public abstract  class GenericResolver {

    protected final List<ChainNode> genericsResolveChain = new ArrayList<>();

    protected GenericResolver() {
        genericsResolveChain.add(new DirectResolverNode());
        genericsResolveChain.add(new ExtendedResolverNode());
    }

    /**
     * Resolve field type arguments and return extended data type for it
     *
     * @param field original field which type arguments need to be resolved to cassandra types
     * @return ExtendedDataType
     * @throws CorruptedMappingException if type argument cannot be resolved to cassandra type or given field doesn't contain type arguments
     */
    protected ExtendedDataType argumentType(Field field, Class<?> genericClass) throws CorruptedMappingException {
        Class<?> fClass = field.getType();

        Optional<Map.Entry<TypeVariable<?>, Type>> type = ReflectionUtils.findType(field, genericClass);

        Map.Entry<TypeVariable<?>, Type> typeEntry =
                type.orElseThrow(() -> new CorruptedMappingException("Cannot resolve type arguments for field: " + field.getName(), field.getDeclaringClass()));


    }
}
