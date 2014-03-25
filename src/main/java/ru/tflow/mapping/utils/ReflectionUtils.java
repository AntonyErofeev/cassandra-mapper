package ru.tflow.mapping.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * Static utility methods
 * <p>
 * Created by erofeev on 12/15/13.
 */
public class ReflectionUtils {

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

    public static Object getFieldValue(Object o, String fieldName) {
        Method m = findMethod(
                o.getClass(),
                "get" + fieldName.replaceFirst(
                        String.valueOf(fieldName.charAt(0)),
                        String.valueOf(Character.toLowerCase(fieldName.charAt(0)))),
                new Class[]{});

        return m == null ? null : invoke(m, o);
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
