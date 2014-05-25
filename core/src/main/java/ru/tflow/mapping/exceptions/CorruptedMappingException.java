package ru.tflow.mapping.exceptions;

/**
 * User: erofeev
 * Date: 11/30/13
 * Time: 9:00 PM
 */
public class CorruptedMappingException extends DatabaseException {

    private Class<?> entity;

    public CorruptedMappingException(String message, Class<?> entity) {
        super(message);
        this.entity = entity;
    }

    public CorruptedMappingException(String message, Throwable cause, Class<?> entity) {
        super(message, cause);
        this.entity = entity;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " class: " + (entity != null ? entity.getName() : "undefined");
    }
}
