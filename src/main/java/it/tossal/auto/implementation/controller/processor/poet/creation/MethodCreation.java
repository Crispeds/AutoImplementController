package it.tossal.auto.implementation.controller.processor.poet.creation;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import it.tossal.auto.implementation.controller.annotation.HandleException;
import it.tossal.auto.implementation.controller.annotation.Mapping;
import it.tossal.auto.implementation.controller.exception.ExceptionHandler;
import it.tossal.auto.implementation.controller.processor.poet.creation.util.TypeElementSupport;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */
public class MethodCreation {

    private static ExecutableElement method;

    private static String serviceReference;

    private static List<TypeElementSupport> typeElementSupports;

    private static TypeElement classDefinition;

    private static final UnaryOperator<MethodSpec.Builder> manageServiceCall = (builder) -> {
        BinaryOperator<String> formatCall = (functionName, methods) -> functionName+"("+methods+")";
        Mapping mapping = method.getAnnotation(Mapping.class);
        String returnString = "";
        AtomicReference<String> methods = new AtomicReference<>("");
        method.getParameters().forEach(variableElement -> methods.updateAndGet(v -> v + variableElement.getSimpleName()));
        if (!method.getReturnType().getKind().equals(TypeKind.VOID))
            returnString = "return ";
        if (mapping==null || mapping.methodCall()==null || mapping.methodCall().isEmpty()){
            return builder.addStatement(formatCall.apply(returnString + serviceReference + "." + method.getSimpleName().toString(),methods.get()));
        }
        return builder.addStatement(returnString + serviceReference + "." + mapping.methodCall());
    };

    private static final UnaryOperator<MethodSpec.Builder> manageStartExceptionHandler = (builder) -> {
        HandleException exceptionHandler = classDefinition.getAnnotation(HandleException.class);
        if (exceptionHandler==null)
            return builder;
        return builder.beginControlFlow("try");
    };

    private static final UnaryOperator<MethodSpec.Builder> manageEndExceptionHandler = (builder) -> {
        HandleException exceptionHandler = classDefinition.getAnnotation(HandleException.class);
        if (exceptionHandler==null)
            return builder;
        builder.nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("$T.handle("+typeElementSupports.get(1).getClassReferenceName()+",e)", ExceptionHandler.class)
                .endControlFlow();
        TypeKind returnType = method.getReturnType().getKind();
        if (returnType.equals(TypeKind.INT) ||
                returnType.equals(TypeKind.BYTE) ||
                returnType.equals(TypeKind.SHORT) ||
                returnType.equals(TypeKind.LONG))
            return builder.addStatement("return 0");
        if (returnType.equals(TypeKind.FLOAT) || returnType.equals(TypeKind.DOUBLE)) return builder.addStatement("return 0.0");
        if (returnType.equals(TypeKind.VOID)) return builder;
        return builder.addStatement("return null");
    };

    private static final UnaryOperator<MethodSpec.Builder> cloneAnnotations = (builder -> {
        for (AnnotationMirror annotationMirror : method.getAnnotationMirrors())
            if(AnnotationToExclude.excludeAnnotations(annotationMirror))
                builder.addAnnotation(AnnotationSpec.get(annotationMirror));
        return builder;
    });

    public static MethodSpec generatePoetMethod(TypeElement classDefinition, ExecutableElement method, List<TypeElementSupport> typeElementSupports){
        MethodCreation.typeElementSupports = typeElementSupports;
        MethodCreation.method = method;
        MethodCreation.serviceReference = typeElementSupports.get(0).getClassReferenceName();
        MethodCreation.classDefinition = classDefinition;
        return manageStartExceptionHandler
                .andThen(manageServiceCall)
                .andThen(manageEndExceptionHandler)
                .andThen(cloneAnnotations)
                .apply(MethodSpec.overriding(method))
                .build();
    }

}
