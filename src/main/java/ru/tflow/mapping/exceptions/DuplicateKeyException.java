package ru.tflow.mapping.exceptions;

/**
 * User: erofeev
 * Date: 11/30/13
 * Time: 8:59 PM
 */
public class DuplicateKeyException extends DatabaseException {

    private Object key;

    private Class<?> entity;

    public DuplicateKeyException(String message, Object key, Class<?> entity) {
        super(message);
        this.key = key;
        this.entity = entity;
    }

    public DuplicateKeyException(String message, Throwable cause, Object key, Class<?> entity) {
        super(message, cause);
        this.key = key;
        this.entity = entity;
    }
}
