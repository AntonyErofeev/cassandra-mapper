package ru.tflow.mapping;

import ru.tflow.mapping.annotations.Compound;
import ru.tflow.mapping.annotations.Id;
import ru.tflow.mapping.exceptions.CorruptedMappingException;
import ru.tflow.mapping.resolvers.MappingResolver;

import java.lang.reflect.Field;

/**
 * Class holding extracted field metadata
 * <p/>
 * User: erofeev
 * Date: 11/30/13
 * Time: 9:08 PM
 */
public class FieldMetadata {

    /**
     * Corresponding field object
     */
    private Field field;

    /**
     * Name of corresponding table column
     */
    private String name;

    /**
     * Extended field metadata
     */
    private ExtendedDataType fieldType;

    public FieldMetadata(Field field, MappingResolver resolver) {
        this.field = field;
        this.field.setAccessible(true);
        ru.tflow.mapping.annotations.Field f = field.getAnnotation(ru.tflow.mapping.annotations.Field.class);
        name = f != null ? f.value() : field.getName().toLowerCase();
        fieldType = resolver.resolve(field).orElseThrow(() -> new CorruptedMappingException("Cannot resolve field type.", field.getType()));
    }

    public Field getField() {
        return field;
    }

    public Class<?> getFClass() {
        return field.getType();
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
        return field.getAnnotation(Compound.class) != null;
    }

    public int compositeOrder() {
        return !isInComposite() ? -1 : field.getAnnotation(Compound.class).value();
    }

}
