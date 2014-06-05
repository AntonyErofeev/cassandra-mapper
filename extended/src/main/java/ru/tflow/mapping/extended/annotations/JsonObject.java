package ru.tflow.mapping.extended.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that field having it should be serialized to json string and stored
 *
 * User: nagakhl
 * Date: 05.06.2014
 * Time: 15:39
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonObject {
}
