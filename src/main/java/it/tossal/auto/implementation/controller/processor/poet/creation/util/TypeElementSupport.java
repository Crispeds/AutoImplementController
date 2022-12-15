package it.tossal.auto.implementation.controller.processor.poet.creation.util;

import javax.lang.model.element.TypeElement;
import java.util.Locale;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */
public class TypeElementSupport {

    private TypeElement typeElement;

    public TypeElementSupport(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public String getClassName(){
        return typeElement.getSimpleName().toString();
    }

    public String getClassReferenceName(){
        return getClassName().substring(0,1).toLowerCase(Locale.ROOT)+getClassName().substring(1);
    }

}
