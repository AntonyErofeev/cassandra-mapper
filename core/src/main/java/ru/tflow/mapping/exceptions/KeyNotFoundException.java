package ru.tflow.mapping.exceptions;

/**
 * User: erofeev
 * Date: 11/30/13
 * Time: 8:57 PM
 */
public class KeyNotFoundException extends DatabaseException {

    private Object key;

    private Class<?> entity;

    public KeyNotFoundException(String message, Object key, Class<?> entity) {
        super(message);
        this.key = key;
        this.entity = entity;
    }

    public KeyNotFoundException(String message, Throwable cause, Object key, Class<?> entity) {
        super(message, cause);
        this.key = key;
        this.entity = entity;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " entity: " +
            (entity != null ? entity.getName() : "Undefined") + "[" +
            (key != null ? key.toString() : "undefined") + "]";
    }
}
