package ru.tflow.mapping;

import ru.tflow.mapping.annotations.Transitional;
import ru.tflow.mapping.exceptions.CorruptedMappingException;
import ru.tflow.mapping.annotations.Table;
import ru.tflow.mapping.resolvers.MappingResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.tflow.mapping.utils.ReflectionUtils.doWithFields;

/**
 * Class holding extracted entity metadata such as field types, primary key field reference, compound keys references etc.
 * <p/>
 * User: erofeev
 * Date: 12/1/13
 * Time: 3:52 PM
 */
public class EntityMetadata {

    /**
     * Class of mapped entity
     */
    private Class<?> eClass;

    /**
     * Field metadata for primary key
     */
    private FieldMetadata primaryKey = null;

    /**
     * Metadata for compound keys sorted according to order information from annotations
     */
    private List<FieldMetadata> keys = new ArrayList<>();

    /**
     * Metadata for _all_ fields in mapped class. Primary key always comes first in this list
     */
    private List<FieldMetadata> fields = new ArrayList<>();

    public EntityMetadata(Class<?> cls, MappingResolver resolver) {
        this.eClass = cls;
        doWithFields(cls, (field) -> {
            if (field.getAnnotation(Transitional.class) == null) {
                FieldMetadata m = new FieldMetadata(field, resolver);
                if (m.isKey()) {
                    if (primaryKey == null) {
                        primaryKey = m;
                        fields.add(0, m);
                    } else {
                        throw new CorruptedMappingException("Entity has several @Id annotations.", cls);
                    }
                } else {
                    if (m.isInComposite()) {
                        keys.add(m);
                    }
                    fields.add(m);
                }
            }
        });

        keys.sort((e1, e2) -> {
            if (e1.compositeOrder() == e2.compositeOrder()) {
                throw new CorruptedMappingException("Two fields with equal composite order found.", eClass);
            }
            return e1.compositeOrder() > e2.compositeOrder() ? 1 : -1;
        });

        if (primaryKey == null) {
            throw new CorruptedMappingException("Entity has no @Id annotation. Cannot operate on entity without primary key", eClass);
        }
    }

    public Class<?> getEntityClass() {
        return eClass;
    }

    public String getTable() {
        Table t = eClass.getAnnotation(Table.class);
        return t != null ? t.value() : eClass.getSimpleName().toLowerCase();
    }

    public FieldMetadata getPrimaryKey() {
        return primaryKey;
    }

    public List<FieldMetadata> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    public List<FieldMetadata> getFields() {
        return Collections.unmodifiableList(fields);
    }
}
