package ru.tflow.mapping.resolvers;

import org.apache.commons.lang3.reflect.TypeUtils;
import ru.tflow.mapping.ExtendedDataType;
import ru.tflow.mapping.exceptions.CorruptedMappingException;
import ru.tflow.mapping.utils.ReflectionUtils;
import ru.tflow.mapping.utils.Tuple2;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Base class for resolvers that need not only resolve type but also it's type arguments.
 *
 * User: nagakhl
 * Date: 27.05.2014
 * Time: 18:08
 */
public abstract  class GenericResolver {

    protected final List<ClassResolver> genericsResolveChain = new ArrayList<>();

    protected GenericResolver() {
        genericsResolveChain.add(new DirectResolverNode());
        genericsResolveChain.add(new ExtendedResolverNode());
    }

    /**
     * Resolve field type arguments and return extended data type for it
     *
     * @param field original field which type arguments need to be resolved to cassandra types
     * @param genericClass Class for which type variables should be resolved.
     *                     For example if field is of type A<M> extends List<SomeClass> then to get type of SomeClass List.class should be provided
     * @return Map with type variable names for given class as names and ExtendedDataTypes for those variables as values
     * @throws CorruptedMappingException if type argument cannot be resolved to cassandra type or given field doesn't contain type arguments
     */
    protected Map<String, ExtendedDataType> argumentType(Field field, Class<?> genericClass) throws CorruptedMappingException {
        Class<?> fClass = field.getType();

        if (!genericClass.isAssignableFrom(field.getType()))
            throw new CorruptedMappingException("Field: " + field.getName() + " cannot be cast to: " + genericClass.getName(), field.getDeclaringClass());

        return TypeUtils.getTypeArguments(field.getType(), genericClass).entrySet().stream()
                .filter(e -> e.getKey().getGenericDeclaration().equals(genericClass))
                .map(e -> {
                    Optional<ExtendedDataType> type = resolveGenericParam((Class<?>) e.getValue());
                    if (!type.isPresent()) {
                        throw new CorruptedMappingException("Generic type cannot be resolved for field: " + field.getName(), field.getDeclaringClass());
                    }
                    return new Tuple2<>(e.getKey().getName(), type.get());
                }).collect(Collectors.toMap(Tuple2::getElement1, Tuple2::getElement2));
    }

    protected Optional<ExtendedDataType> resolveGenericParam(Class<?> param) {
        for (ClassResolver resolver : genericsResolveChain) {
            Optional<ExtendedDataType> result = resolver.resolve(param);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
