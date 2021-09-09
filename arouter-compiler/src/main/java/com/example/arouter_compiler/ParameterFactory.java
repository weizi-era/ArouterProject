package com.example.arouter_compiler;

import com.example.arouter_annotation.Parameter;
import com.example.arouter_compiler.utils.ProcessorConfig;
import com.example.arouter_compiler.utils.ProcessorUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 *  目的：生成以下代码：
 *
 *         @Override
 *         public void getParameter(Object targetParameter) {
 *               Personal_MainActivity t = (Personal_MainActivity) targetParameter;
 *               t.name = t.getIntent().getStringExtra("name");
 *               t.sex = t.getIntent().getStringExtra("sex");
 *         }
 *
 */
public class ParameterFactory {

     private MethodSpec.Builder method;

     private ClassName className;

     private Messager messager;

     private Types typeUtils;

     private Elements elementUtils;

     private TypeMirror callMirror;

     private ParameterFactory(Builder builder) {
        this.className = builder.className;
        this.messager = builder.messager;
        this.typeUtils = builder.typeUtils;

        method = MethodSpec.methodBuilder(ProcessorConfig.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);

        // call 自描述
        this.callMirror = builder.elementUtils
                .getTypeElement(ProcessorConfig.CALL_API_NAME)
                .asType();
     }

     public void addFirstStatement() {
         method.addStatement("$T t = ($T) " + ProcessorConfig.PARAMETER_NAME, className, className);
     }

     public MethodSpec build() {
         return method.build();
     }

     public void buildStatement(Element element) {

         // 遍历注解的属性节点 生成函数体
         TypeMirror typeMirror = element.asType();

         // 获取 TypeKind 枚举类型的序列号
         int type = typeMirror.getKind().ordinal();

         //  获取属性名
         String fieldName = element.getSimpleName().toString();
         // 获取注解的值
         String annotationValue = element.getAnnotation(Parameter.class).name();
         // 判断注解的值为空的情况
         annotationValue = ProcessorUtils.isEmpty(annotationValue) ? fieldName : annotationValue;

         // todo 最终拼接的前缀
         String finalValue = "t." + fieldName;

         // todo 拼接：t.name = t.getIntent().
         String methodContent = finalValue + " = t.getIntent().";

         //todo  这里是区分int boolean类型 都有默认值 而String 没有默认值
         if (type == TypeKind.INT.ordinal()) {
             methodContent += "getIntExtra($S, " + finalValue + ")";
         } else if (type == TypeKind.BOOLEAN.ordinal()) {
             methodContent += "getBooleanExtra($S, " + finalValue + ")";
         } else {
             if (typeMirror.toString().equalsIgnoreCase(ProcessorConfig.STRING_PACKAGE)) {
                 // String类型
                 methodContent += "getStringExtra($S)";
             } else if (typeUtils.isSubtype(typeMirror, callMirror)) {
                 methodContent = "t." + fieldName + " = ($T) $T.getInstance().build($S).navigation(t)";

                 method.addStatement(methodContent,
                         TypeName.get(typeMirror),
                         ClassName.get(ProcessorConfig.AROUTER_API_PACKAGE, ProcessorConfig.AROUTER_MANAGER),
                         annotationValue);

                 return;
             }
         }

         if (methodContent.endsWith(")")) {
             method.addStatement(methodContent, annotationValue);
         } else {
             messager.printMessage(Diagnostic.Kind.ERROR, "目前暂支持String、int、boolean传参");
         }
     }

     public static class Builder {

         private ParameterSpec parameterSpec;

         private ClassName className;

         private Messager messager;

         private Elements elementUtils;

         private Types typeUtils;

         public Builder setElements(Elements elementUtils) {
             this.elementUtils = elementUtils;
             return this;
         }

         public Builder setTypes(Types typeUtils) {
             this.typeUtils = typeUtils;
             return this;
         }

         public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
         }

         public Builder setClassName(ClassName className) {
             this.className = className;
             return this;
         }

         public Builder setMessager(Messager messager) {
             this.messager = messager;
             return this;
         }

         public ParameterFactory build() {
             if (parameterSpec == null) {
                 throw new IllegalArgumentException("parameterSpec方法参数体为空");
             }

             if (className == null) {
                 throw new IllegalArgumentException("className为空");
             }

             if (messager == null) {
                 throw new IllegalArgumentException("messager为空");
             }

             return new ParameterFactory(this);
         }
     }
}
