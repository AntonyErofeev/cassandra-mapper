package ru.tflow.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation to map entity field to column with different name
 * <p>
 * User: erofeev
 * Date: 11/24/13
 * Time: 3:38 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    String value() default "";

}
