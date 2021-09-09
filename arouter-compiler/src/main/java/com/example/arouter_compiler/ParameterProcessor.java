package com.example.arouter_compiler;

import com.example.arouter_annotation.Parameter;
import com.example.arouter_compiler.utils.ProcessorConfig;
import com.example.arouter_compiler.utils.ProcessorUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)  // 开启服务
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ProcessorConfig.PARAMETER_PACKAGE})
public class ParameterProcessor extends AbstractProcessor {

    private Elements elementUtils; // 类信息
    private Types typeUtils;  // 具体类型
    private Messager messager;  // 日志
    private Filer filer;  // 生成器

    // todo key: activity
    //      value:字段集合
    private Map<TypeElement, List<Element>> parameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!ProcessorUtils.isEmpty(annotations)) {

            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Parameter.class);

            if (!ProcessorUtils.isEmpty(elements)) {
                for (Element element: elements) {
                    //  拿到字段节点的上一个节点：类节点
                    TypeElement typeElement = (TypeElement) element.getEnclosingElement();

                    if (parameterMap.containsKey(typeElement)) {
                        parameterMap.get(typeElement).add(element);
                    } else {
                        List<Element> fields = new ArrayList<>();
                        fields.add(element);
                        parameterMap.put(typeElement, fields);  // 加入缓存
                    }
                }

                if (ProcessorUtils.isEmpty(parameterMap)) {
                    return true;
                }

                TypeElement activityType = elementUtils.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
                TypeElement parameterType = elementUtils.getTypeElement(ProcessorConfig.PARAMETER_API_GET);

                // 生成方法
                ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, ProcessorConfig.PARAMETER_NAME).build();

                for (Map.Entry<TypeElement, List<Element>> entry : parameterMap.entrySet()) {

                    TypeElement typeElement = entry.getKey();

                    if (!typeUtils.isSubtype(typeElement.asType(), activityType.asType())) {
                        // 不匹配抛出异常，这里谨慎使用！考虑维护问题
                        throw new RuntimeException("@ARouter注解目前仅限用于Activity类之上");
                    }

                    // 是activity
                    ClassName className = ClassName.get(typeElement);

                    ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                            .setClassName(className)
                            .setMessager(messager)
                            .build();

                    factory.addFirstStatement();

                    for (Element element : entry.getValue()) {
                        factory.buildStatement(element);
                    }

                    String finalClassName = typeElement.getSimpleName() + ProcessorConfig.PARAMETER_FILE_NAME;
                    // 生成java文件
                    try {
                        JavaFile.builder(className.packageName(), TypeSpec.classBuilder(finalClassName)
                                .addModifiers(Modifier.PUBLIC)
                                .addSuperinterface(ClassName.get(parameterType))
                                .addMethod(factory.build())
                                .build())
                                .build().writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
