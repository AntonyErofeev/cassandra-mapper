package ru.tflow.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that field should be included in composite key
 * <p/>
 * User: erofeev
 * Date: 11/24/13
 * Time: 3:36 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Compound {

    /**
     * Order in which field appear in composite key
     *
     * @return order number
     */
    int value() default 0;

}
