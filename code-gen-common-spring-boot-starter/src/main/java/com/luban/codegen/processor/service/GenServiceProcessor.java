package com.luban.codegen.processor.service;


import com.google.auto.service.AutoService;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.model.PageRequestWrapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.springframework.data.domain.Page;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author gim
 */
@AutoService(value = CodeGenProcessor.class)
public class GenServiceProcessor extends AbstractCodeGenProcessor {

    public static final String SERVICE_SUFFIX = "Service";

    public static final String SERVICE_PREFIX = "I";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        String className = SERVICE_PREFIX + typeElement.getSimpleName() + SERVICE_SUFFIX;
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className)
                .addModifiers(Modifier.PUBLIC);
        DefaultNameContext nameContext = getNameContext(typeElement);
        Optional<MethodSpec> createMethod = createMethod(typeElement, nameContext);
        createMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> updateMethod = updateMethod(typeElement, nameContext);
        updateMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> validMethod = validMethod(typeElement);
        validMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> invalidMethod = invalidMethod(typeElement);
        invalidMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> findByIdMethod = findByIdMethod(nameContext);
        findByIdMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> findByPageMethod = findByPageMethod(nameContext);
        findByPageMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        generateJavaSourceFile(generatePackage(typeElement),
                typeElement.getAnnotation(GenService.class).sourcePath(), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenService.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenService.class).pkgName();
    }

    private Optional<MethodSpec> createMethod(TypeElement typeElement,
                                              DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                    .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "creator")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addJavadoc("create")
                    .returns(Long.class).build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> updateMethod(TypeElement typeElement,
                                              DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                    .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "updater")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addJavadoc("update")
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> validMethod(TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("valid" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addJavadoc("valid")
                .build());
    }

    private Optional<MethodSpec> invalidMethod(TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("invalid" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addJavadoc("invalid")
                .build());
    }

    private Optional<MethodSpec> findByIdMethod(DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getVoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("findById")
                    .addParameter(Long.class, "id")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addJavadoc("findById")
                    .returns(ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()))
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> findByPageMethod(DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName(),
                nameContext.getVoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("findByPage")
                    .addParameter(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class), ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName())), "query")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addJavadoc("findByPage")
                    .returns(ParameterizedTypeName.get(ClassName.get(Page.class),
                            ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())))
                    .build());
        }
        return Optional.empty();
    }

}