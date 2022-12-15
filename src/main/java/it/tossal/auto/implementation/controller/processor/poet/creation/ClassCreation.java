package it.tossal.auto.implementation.controller.processor.poet.creation;

import com.squareup.javapoet.*;
import it.tossal.auto.implementation.controller.processor.poet.creation.util.TypeElementSupport;
import it.tossal.processor.management.EnvironmentManagement;
import org.springframework.beans.factory.annotation.Autowired;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */
public class ClassCreation {

    private static List<TypeElementSupport> typeElementSupports;

    private static List<MethodSpec> methodSpecs;

    private static String classImplName;

    private static TypeElement classDefinition;

    private static EnvironmentManagement environmentManagement;

    private static UnaryOperator<TypeSpec.Builder> createFields = (builder) -> {
        for (TypeElementSupport tes : typeElementSupports)
            builder.addField(FieldSpec.builder(TypeName.get(tes.getTypeElement().asType()), tes.getClassReferenceName(),Modifier.PRIVATE)
                    .addAnnotation(Autowired.class)
                    .build());
        return builder;
    };

    private static UnaryOperator<TypeSpec.Builder> createAnnotations = (builder -> {
        for (AnnotationMirror annotationMirror : classDefinition.getAnnotationMirrors())
            if(AnnotationToExclude.excludeAnnotations(annotationMirror))
                builder.addAnnotation(AnnotationSpec.get(annotationMirror));
        return builder;
    });

    public static TypeSpec createTypeSpec(EnvironmentManagement environmentManagement,TypeElement classDefinition, String className, List<MethodSpec> methodSpecs, List<TypeElementSupport> typeElementSupports){
        ClassCreation.classImplName = className+"Impl";
        ClassCreation.methodSpecs=methodSpecs;
        ClassCreation.typeElementSupports=typeElementSupports;
        ClassCreation.classDefinition = classDefinition;
        ClassCreation.environmentManagement = environmentManagement;
        return createFields
                .andThen(createAnnotations)
                .apply(TypeSpec.classBuilder(classImplName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(TypeName.get(classDefinition.asType())))
                        .addMethods(methodSpecs)
                .build();
    }

}
