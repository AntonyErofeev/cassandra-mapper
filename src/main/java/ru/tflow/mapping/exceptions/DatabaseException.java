package ru.tflow.mapping.exceptions;

/**
 * Exception indicating that there was an error while executing database operations
 * <p>
 * User: erofeev
 * Date: 11/30/13
 * Time: 8:53 PM
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
