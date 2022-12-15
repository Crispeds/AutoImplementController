package it.tossal.auto.implementation.controller.processor.poet.creation;

import it.tossal.auto.implementation.controller.annotation.AutoImplementController;
import it.tossal.auto.implementation.controller.annotation.HandleException;
import it.tossal.auto.implementation.controller.annotation.Mapping;

import javax.lang.model.element.AnnotationMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */
public class AnnotationToExclude {

    static private final List<Class<? extends Annotation>> classToExclude = new ArrayList<>();
    static {
        classToExclude.add(AutoImplementController.class);
        classToExclude.add(HandleException.class);
        classToExclude.add(Mapping.class);
    }

    public static boolean excludeAnnotations(AnnotationMirror annotationMirror){
        String annotationName = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
        return classToExclude.stream().noneMatch(aClass -> annotationName.equals(aClass.getSimpleName()));
    }

}
