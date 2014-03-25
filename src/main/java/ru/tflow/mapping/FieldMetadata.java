package ru.tflow.mapping;

import ru.tflow.mapping.annotations.Composite;
import ru.tflow.mapping.annotations.Id;
import ru.tflow.mapping.exceptions.CorruptedMappingException;

import java.lang.reflect.Field;

/**
 * User: erofeev
 * Date: 11/30/13
 * Time: 9:08 PM
 */
public class FieldMetadata {

    private Field field;

    private Class<?> fClass;

    private String name;

    private ExtendedDataType fieldType;

    public FieldMetadata(Field field, MappingResolver resolver) {
        this.field = field;
        this.field.setAccessible(true);
        this.fClass = field.getType();
        ru.tflow.mapping.annotations.Field f = field.getAnnotation(ru.tflow.mapping.annotations.Field.class);
        name = f != null ? f.value() : field.getName().toLowerCase();
        fieldType = resolver.resolve(field).orElseThrow(() -> new CorruptedMappingException("Cannot resolve field type.", fClass));
    }

    public Field getField() {
        return field;
    }

    public Class<?> getFClass() {
        return fClass;
    }

    public String getName() {
        return name;
    }

    public ExtendedDataType getFieldType() {
        return fieldType;
    }

    public boolean isKey() {
        return field.getAnnotation(Id.class) != null;
    }

    public boolean isInComposite() {
        return field.getAnnotation(Composite.class) != null;
    }

    public int compositeOrder() {
        return !isInComposite() ? -1 : field.getAnnotation(Composite.class).value();
    }

}
