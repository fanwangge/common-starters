package com.luban.codegen.processor;

import cn.hutool.core.collection.CollUtil;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.context.ProcessingEnvironmentContextHolder;
import com.luban.codegen.processor.api.GenFeign;
import com.luban.codegen.processor.command.create.GenCreateCommand;
import com.luban.codegen.processor.command.create.GenCreateCommandProcessor;
import com.luban.codegen.processor.command.update.GenUpdateCommand;
import com.luban.codegen.processor.command.update.GenUpdateCommandProcessor;
import com.luban.codegen.processor.context.create.GenCreateContext;
import com.luban.codegen.processor.context.create.GenCreateContextProcessor;
import com.luban.codegen.processor.context.update.GenUpdateContext;
import com.luban.codegen.processor.context.update.GenUpdateContextProcessor;
import com.luban.codegen.processor.controller.GenController;
import com.luban.codegen.processor.controller.GenControllerProcessor;
import com.luban.codegen.processor.dto.GenDto;
import com.luban.codegen.processor.dto.jpa.GenDtoProcessor;
import com.luban.codegen.processor.event.GenEvent;
import com.luban.codegen.processor.event.GenEventListener;
import com.luban.codegen.processor.event.GenEventListenerProcessor;
import com.luban.codegen.processor.event.GenEventProcessor;
import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.mapper.GenMapperProcessor;
import com.luban.codegen.processor.modifier.FieldSpecModifier;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.repository.jpa.GenRepositoryProcessor;
import com.luban.codegen.processor.request.*;
import com.luban.codegen.processor.response.GenPageResponse;
import com.luban.codegen.processor.response.GenPageResponseProcessor;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.processor.response.GenResponseProcessor;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.processor.service.jpa.GenServiceImplProcessor;
import com.luban.codegen.processor.service.jpa.GenServiceProcessor;
import com.luban.codegen.processor.vo.GenVo;
import com.luban.codegen.processor.vo.jpa.GenVoProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.common.base.annotations.FieldDesc;
import com.squareup.javapoet.*;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
public abstract class AbstractCodeGenProcessor implements CodeGenProcessor {

    protected static final String FILE_COMMENT = "--- Auto Generated By CodeGen Module ---";

    @Getter
    protected DefaultNameContext nameContext;

    @Override
    public void generate(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        nameContext = createNameContext(typeElement);
        generateClass(typeElement, roundEnvironment);
    }

    protected abstract void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment);

    protected List<VariableElement> findFields(TypeElement element, Predicate<VariableElement> predicate) {
        final List<? extends Element> enclosedElements = element.getEnclosedElements();
        final List<VariableElement> variableElements = ElementFilter.fieldsIn(enclosedElements);
        return variableElements.stream().filter(predicate).distinct().collect(Collectors.toList());
    }

    protected Optional<TypeElement> getSuperClass(TypeElement typeElement) {
        final TypeMirror superclass = typeElement.getSuperclass();
        if (superclass instanceof DeclaredType) {
            final Element element = ((DeclaredType) superclass).asElement();
            if (element instanceof TypeElement) {
                return Optional.of(((TypeElement) element));
            }
        }
        return Optional.empty();
    }

    protected void generateGettersAndSettersWithLombok(TypeSpec.Builder builder, Collection<VariableElement> variableElements, Collection<FieldSpecModifier> fieldSpecModifiers) {
        builder.addAnnotation(Data.class);
        variableElements.forEach(ve -> {
            TypeName typeName = null;
            if (CollUtil.isNotEmpty(fieldSpecModifiers)) {
                typeName = fieldSpecModifiers.stream()
                        .filter(modifier -> modifier.isModifiable(ve))
                        .map(modifier -> modifier.modify(ve))
                        .findFirst()
                        .orElse(null);
            }
            Optional.ofNullable(generateField(ve, typeName)).ifPresent(builder::addField);
        });
    }

    protected void generateGettersAndSetters(TypeSpec.Builder builder, Collection<VariableElement> variableElements, Collection<FieldSpecModifier> fieldSpecModifiers) {
        variableElements.forEach(ve -> {
            TypeName typeName = null;
            if (CollUtil.isNotEmpty(fieldSpecModifiers)) {
                typeName = fieldSpecModifiers.stream()
                        .filter(modifier -> modifier.isModifiable(ve))
                        .map(modifier -> modifier.modify(ve))
                        .findFirst()
                        .orElse(null);
            }
            Optional.ofNullable(generateField(ve, typeName)).ifPresent(fieldSpec -> {
                        builder.addField(fieldSpec);
                        generateGettersAndSetters(builder, ve, fieldSpec.type);
                    }
            );
        });
    }

    private void generateGettersAndSetters(TypeSpec.Builder builder, VariableElement ve, TypeName actualTypeName) {
        final String fieldName = ve.getSimpleName().toString();
        final String fieldMethodName = getFieldMethodName(ve);
        final MethodSpec getter = MethodSpec.methodBuilder("get" + fieldMethodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(actualTypeName)
                .addStatement("return $L", fieldName).build();

        final MethodSpec setter = MethodSpec.methodBuilder("set" + fieldMethodName)
                .addParameter(actualTypeName, ve.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("this.$L = $L", fieldName, fieldName).build();

        builder.addMethod(getter);
        builder.addMethod(setter);
    }

    protected FieldSpec generateField(VariableElement ve, TypeName typeName) {
        final TypeName actualTypeName = Optional.ofNullable(typeName).orElse(TypeName.get(ve.asType()));
        final FieldSpec.Builder builder = FieldSpec.builder(actualTypeName, ve.getSimpleName().toString(), Modifier.PRIVATE);
        Optional.ofNullable(ve.getAnnotation(FieldDesc.class)).ifPresent(an ->
                builder.addAnnotation(AnnotationSpec.builder(FieldDesc.class).addMember("value", "$S", an.value()).build())
        );
        return builder.build();
    }

    protected String getFieldMethodName(VariableElement ve) {
        return ve.getSimpleName().toString().substring(0, 1).toUpperCase() + ve.getSimpleName()
                .toString().substring(1);
    }

    protected void generateJavaFile(String packageName, TypeSpec.Builder typeSpecBuilder) {
        final JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build())
                .addFileComment(FILE_COMMENT)
                .build();
        try {
            javaFile.writeTo(ProcessingEnvironmentContextHolder.getEnvironment().getFiler());
        } catch (IOException e) {
            ProcessingEnvironmentContextHolder.getEnvironment().getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    protected void generateJavaSourceFile(String packageName, String path, TypeSpec.Builder typeSpecBuilder) {
        final TypeSpec typeSpec = typeSpecBuilder.build();
        final JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .addFileComment(FILE_COMMENT)
                .build();
        final String fileName = typeSpec.name + ".java";
        final String packagePath = packageName.replace(".", File.separator) + File.separator + fileName;
        try {
            final String absolutePath = Paths.get(path).toFile().getAbsolutePath();
            File file = new File(absolutePath);
            if (!file.exists()) {
                return;
            }
            String sourceFileName = absolutePath + File.separator + packagePath;
            File sourceFile = new File(sourceFileName);
            if (!sourceFile.exists()) {
                javaFile.writeTo(file);
            }
        } catch (IOException e) {
            log.error("generate Java Source File error", e);
        }
    }

    private DefaultNameContext createNameContext(TypeElement typeElement) {
        DefaultNameContext context = new DefaultNameContext();

        String serviceName = GenServiceProcessor.SERVICE_PREFIX + typeElement.getSimpleName() + GenServiceProcessor.SERVICE_SUFFIX;
        String implName = typeElement.getSimpleName() + GenServiceImplProcessor.IMPL_SUFFIX;
        String repositoryName = typeElement.getSimpleName() + GenRepositoryProcessor.REPOSITORY_SUFFIX;

        String mapperName = typeElement.getSimpleName() + GenMapperProcessor.SUFFIX;

        String voName = typeElement.getSimpleName() + GenVoProcessor.SUFFIX;
        String dtoName = typeElement.getSimpleName() + GenDtoProcessor.SUFFIX;

        String responseName = typeElement.getSimpleName() + GenResponseProcessor.RESPONSE_SUFFIX;
        String pageResponseName = typeElement.getSimpleName() + GenPageResponseProcessor.RESPONSE_SUFFIX;

        String requestName = typeElement.getSimpleName() + GenRequestProcessor.SUFFIX;
        String createRequestName = typeElement.getSimpleName() + GenCreateRequestProcessor.SUFFIX;
        String updateRequestName = typeElement.getSimpleName() + GenUpdateRequestProcessor.SUFFIX;
        String pageRequestName = typeElement.getSimpleName() + GenPageRequestProcessor.SUFFIX;

        String controllerName = typeElement.getSimpleName() + GenControllerProcessor.CONTROLLER_SUFFIX;

        String createContextName = GenCreateContextProcessor.PREFIX + typeElement.getSimpleName() + GenCreateContextProcessor.SUFFIX;
        String createCommandName = GenCreateCommandProcessor.PREFIX + typeElement.getSimpleName() + GenCreateCommandProcessor.SUFFIX;
        String updateContextName = GenUpdateContextProcessor.PREFIX + typeElement.getSimpleName() + GenUpdateContextProcessor.SUFFIX;
        String updateCommandName = GenUpdateCommandProcessor.PREFIX + typeElement.getSimpleName() + GenUpdateCommandProcessor.SUFFIX;

        String eventName = typeElement.getSimpleName() + GenEventProcessor.SUFFIX;
        String eventListenerName = typeElement.getSimpleName() + GenEventListenerProcessor.SUFFIX;

        context.setServiceClassName(serviceName);
        context.setServiceImplClassName(implName);

        context.setRepositoryClassName(repositoryName);
        context.setMapperClassName(mapperName);

        context.setVoClassName(voName);
        context.setDtoClassName(dtoName);

        context.setPageResponseClassName(pageResponseName);
        context.setResponseClassName(responseName);

        context.setRequestClassName(requestName);
        context.setCreateRequestClassName(createRequestName);
        context.setUpdateRequestClassName(updateRequestName);
        context.setPageRequestClassName(pageRequestName);

        context.setControllerClassName(controllerName);

        context.setCreateContextClassName(createContextName);
        context.setCreateCommandClassName(createCommandName);
        context.setUpdateContextClassName(updateContextName);
        context.setUpdateCommandClassName(updateCommandName);

        context.setEventClassName(eventName);
        context.setEventListenerClassName(eventListenerName);


        Optional.ofNullable(typeElement.getAnnotation(GenEvent.class)).ifPresent(anno -> context.setEventPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenEventListener.class)).ifPresent(anno -> context.setEventListenerPackageName(anno.pkgName()));

        Optional.ofNullable(typeElement.getAnnotation(GenCreateContext.class)).ifPresent(anno -> context.setCreateContextPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenCreateCommand.class)).ifPresent(anno -> context.setCreateCommandPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenUpdateContext.class)).ifPresent(anno -> context.setUpdateContextPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenUpdateCommand.class)).ifPresent(anno -> context.setUpdateCommandPackageName(anno.pkgName()));

        Optional.ofNullable(typeElement.getAnnotation(GenController.class)).ifPresent(anno -> context.setControllerPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenFeign.class)).ifPresent(anno -> context.setFeignPackageName(anno.pkgName()));

        Optional.ofNullable(typeElement.getAnnotation(GenDto.class)).ifPresent(anno -> context.setDtoPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenVo.class)).ifPresent(anno -> context.setVoPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenRepository.class)).ifPresent(anno -> context.setRepositoryPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenMapper.class)).ifPresent(anno -> context.setMapperPackageName(anno.pkgName()));

        Optional.ofNullable(typeElement.getAnnotation(GenService.class)).ifPresent(anno -> context.setServicePackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenServiceImpl.class)).ifPresent(anno -> context.setServiceImplPackageName(anno.pkgName()));

        Optional.ofNullable(typeElement.getAnnotation(GenRequest.class)).ifPresent(anno -> context.setRequestPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenCreateRequest.class)).ifPresent(anno -> context.setCreateRequestPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenUpdateRequest.class)).ifPresent(anno -> context.setUpdateRequestPackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenPageRequest.class)).ifPresent(anno -> context.setPageRequestPackageName(anno.pkgName()));

        Optional.ofNullable(typeElement.getAnnotation(GenPageResponse.class)).ifPresent(anno -> context.setPageResponsePackageName(anno.pkgName()));
        Optional.ofNullable(typeElement.getAnnotation(GenResponse.class)).ifPresent(anno -> context.setResponsePackageName(anno.pkgName()));

        return context;
    }
}
