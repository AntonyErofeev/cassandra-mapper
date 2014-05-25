package ru.tflow.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation to specify table name for storing entity
 * <p/>
 * User: erofeev
 * Date: 11/24/13
 * Time: 4:42 PM
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String value() default "";

}
