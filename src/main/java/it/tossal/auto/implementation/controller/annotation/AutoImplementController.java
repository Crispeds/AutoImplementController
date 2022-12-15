package it.tossal.auto.implementation.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.CLASS)
public @interface AutoImplementController {
    /**
     * @return Service Class to bind to this controller for service calls.
     */
    Class<?> serviceToBind();
}
