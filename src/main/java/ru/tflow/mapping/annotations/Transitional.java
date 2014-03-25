package ru.tflow.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking field in entity that should be skipped during saving and retrieving from database
 * <p>
 * User: erofeev
 * Date: 11/24/13
 * Time: 4:36 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transitional {
}
