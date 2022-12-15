package it.tossal.auto.implementation.controller.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import it.tossal.auto.implementation.controller.annotation.AutoImplementController;
import it.tossal.auto.implementation.controller.annotation.HandleException;
import it.tossal.auto.implementation.controller.processor.poet.creation.ClassCreation;
import it.tossal.auto.implementation.controller.processor.poet.creation.MethodCreation;
import it.tossal.auto.implementation.controller.processor.poet.creation.util.TypeElementSupport;
import it.tossal.processor.management.AbstractAnnotationProcessor;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="https://www.linkedin.com/in/federico-tosello/">Tosello Federico</a>
 * @version 1.0.0
 * @since JAVA 17
 */
@AutoService(Processor.class)
public class AutoImplementControllerProcessor extends AbstractAnnotationProcessor {

    protected Set<Class<? extends Annotation>> getClassSupportedAnnotationTypes() {
        Set<Class<? extends Annotation>> classSet = new HashSet<>();
        classSet.add(AutoImplementController.class);
        return classSet;
    }

    protected SourceVersion getSourceVersionSupported() {
        return SourceVersion.latestSupported();
    }

    protected void processSingleElements(Element element) {

        TypeElement classDefinition = (TypeElement) element;

        String nameOfTheControllerClass = classDefinition.getSimpleName().toString();

        PackageElement packageOfTheControllerClass = environmentManagement.getElementUtils().getPackageOf(element);

        List<TypeElementSupport> typeElementSupports = new ArrayList<>();
        typeElementSupports.add(getTypeElementService(element));

        if (element.getAnnotation(HandleException.class)!= null)
            typeElementSupports.add(getTypeElementMapping(element));


        List<? extends Element> methods = classDefinition.getEnclosedElements().stream().filter(this::checkIfElementIsAMethod).collect(Collectors.toList());

        List<MethodSpec> methodSpecs = methods.stream().map(elementMethod -> generatePoetMethod(classDefinition, elementMethod, typeElementSupports)).collect(Collectors.toList());

        try {
            environmentManagement.getMessager().printMessage(Diagnostic.Kind.WARNING,"Implementing in package "+ packageOfTheControllerClass +" the following methods: "+methodSpecs,element);
            writeFile(packageOfTheControllerClass, ClassCreation.createTypeSpec(environmentManagement,classDefinition, nameOfTheControllerClass,methodSpecs,typeElementSupports));
        } catch (IOException e) {
            environmentManagement.getMessager().printMessage(Diagnostic.Kind.ERROR,"Error while writing file "+e.getMessage(),element);
        }

    }

    private TypeElementSupport getTypeElementService(Element element){
        try {
            Class<?> clazzReference = element.getAnnotation(AutoImplementController.class).serviceToBind();
            return new TypeElementSupport(environmentManagement.getElementUtils().getTypeElement(clazzReference.getCanonicalName()));
        }catch (MirroredTypeException mte){
            return new TypeElementSupport((TypeElement)((DeclaredType) mte.getTypeMirror()).asElement());
        }
    }

    private TypeElementSupport getTypeElementMapping(Element element){
        try {
            Class<?> clazzReference = element.getAnnotation(HandleException.class).exceptionMapper();
            return new TypeElementSupport(environmentManagement.getElementUtils().getTypeElement(clazzReference.getCanonicalName()));
        }catch (MirroredTypeException mte){
            return new TypeElementSupport((TypeElement)((DeclaredType) mte.getTypeMirror()).asElement());
        }
    }

    private MethodSpec generatePoetMethod(TypeElement classDefinition, Element element,List<TypeElementSupport> typeElementSupports){
        return MethodCreation.generatePoetMethod(classDefinition, (ExecutableElement) element,typeElementSupports);
    }

    private Boolean checkIfElementIsAMethod(Element element){
        return element.getKind().equals(ElementKind.METHOD);
    }

    private void writeFile(PackageElement packageElement, TypeSpec typeSpec) throws IOException {
        JavaFile.builder(String.valueOf(packageElement), typeSpec)
                .build()
                .writeTo(environmentManagement.getFiler());
    }

    protected void checkIfElementIsEligibleForProcessing(Element element) throws IOException {
        if (!element.getKind().equals(ElementKind.INTERFACE))
            throw new IOException("Wrong kind of element");
    }

}
