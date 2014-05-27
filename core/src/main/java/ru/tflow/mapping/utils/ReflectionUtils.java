package ru.tflow.mapping.utils;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * Static utility methods
 * <p/>
 * Created by erofeev on 12/15/13.
 */
public class ReflectionUtils {

    protected static Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    public static Object instantiate(Class cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void setField(Field f, Object o, Object value) {
        try {
            f.setAccessible(true);
            f.set(o, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Method findMethod(Class<?> cls, String name, Class<?>... args) {
        List<Method> methods = Arrays.asList(cls.getDeclaredMethods());
        List<Method> opt = methods
            .stream()
            .filter(m -> m.getName().equals(name) && Arrays.equals(m.getParameterTypes(), args))
            .collect(toList());

        if (!opt.isEmpty()) {
            return opt.get(0);
        } else {
            if (cls.getSuperclass().isAssignableFrom(Object.class)) {
                return null;
            } else {
                return findMethod(cls.getSuperclass(), name, args);
            }
        }
    }

    public static Optional<Map.Entry<TypeVariable<?>, Type>> findType(java.lang.reflect.Field f, Class<?> cls) {
        Map<TypeVariable<?>, Type> args = TypeUtils.getTypeArguments(f.getGenericType(), cls);
        return args.entrySet()
            .stream()
            .filter(v -> v.getKey().getGenericDeclaration().equals(cls))
            .findFirst();
    }

    public static boolean isGenericType(Class<?> cls) {
        if (cls.isAssignableFrom(Object.class)) {
            return false;
        } else {
            return cls.getTypeParameters().length != 0 || isGenericType(cls.getSuperclass());
        }
    }

    public static Object readField(Field f, Object o) {
        try {
            return FieldUtils.readField(f, o, true);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            log.error("=====> Cannot read field {} of object {}. Error is: {}", f.getName(), o, e.getMessage());
            return null;
        }
    }

    public static Object invoke(Method m, Object o, Object... params) {
        try {
            return m.invoke(o, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static void doWithFields(Class<?> c, Consumer<Field> operation) {
        for (Field f : c.getDeclaredFields()) {
            f.setAccessible(true);
            operation.accept(f);
        }
        if (!c.getSuperclass().isAssignableFrom(Object.class)) {
            doWithFields(c.getSuperclass(), operation);
        }
    }

}
