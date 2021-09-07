package com.example.arouter_compiler;


import com.example.arouter_annotation.ARouter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)  // 启动服务
@SupportedAnnotationTypes({"com.example.arouter_annotation.ARouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)

@SupportedOptions("zhaojiawei")  // 接收安卓工程传递的参数
public class ARouterProcessor extends AbstractProcessor {


    /**
     * 类对象                      说明
     * MethodSpec       代表一个构造函数或方法声明
     * TypeSpec         代表一个类，接口，或者枚举声明
     * FieldSpec        代表一个成员变量，一个字段声明
     * JavaFile         包含一个顶级类的Java文件
     * ParameterSpec        用来创建参数
     * AnnotationSpec       用来创建注解
     * ClassName            用来包装一个类
     * TypeName         类型，如在添加返回值类型是使用 TypeName.VOID
     * $S 字符串，如：$S, ”hello”
     * $T 类、接口，如：$T, MainActivity
     */

    private Elements elementTool;
    private Types typesTool;
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementTool = processingEnv.getElementUtils();
        typesTool = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        String value = processingEnv.getOptions().get("zhaojiawei");
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, ">>>>>>>>>>>>>>>>>>>" + value);
    }

    // 如果没有在任何地方使用注解，此函数不会执行。
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.WARNING, ">>>>>>>>>>>>>>>>>>> zhaojiawei");

        if (annotations.isEmpty()) {
            return false;
        }

        // 拿到注解
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);


        for (Element element : elements) {

            /**
             模板一
             package com.example.helloworld;

             public final class HelloWorld {
                 public static void main(String[] args) {
                    System.out.println("Hello, JavaPoet!");
                 }
             }

             public class MainActivity extends AppCompatActivity {

                @Override
                protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    setContentView(R.layout.activity_main);
                }
            }

             */

//            ClassName bundle = ClassName.get("android.os", "Bundle");
//            ClassName override = ClassName.get("java.lang", "Override");
//            ClassName appCompatActivity = ClassName.get("androidx.appcompat.app", "AppCompatActivity");
//
//            // 1.方法
//            MethodSpec onCreate = MethodSpec.methodBuilder("onCreate")
//                    .addModifiers(Modifier.PROTECTED)
//                    .returns(void.class)
//                    .addAnnotation(override)
//                    .addParameter(bundle, "savedInstanceState")
//                    .addStatement("super.onCreate(savedInstanceState)")
//                    .addStatement("setContentView(R.layout.activity_main)")
//                    .build();
//
//            // 2.类
//            TypeSpec mainActivity = TypeSpec.classBuilder("MainActivity")
//                    .addMethod(onCreate)
//                    .superclass(appCompatActivity)
//                    .addModifiers(Modifier.PUBLIC)
//                    .build();
//
//            // 3.包
//            JavaFile packagef = JavaFile.builder("com.example.zjw", mainActivity)
//                    .build();
//
//            // 生成文件
//            try {
//                packagef.writeTo(filer);
//            } catch (IOException e) {
//                e.printStackTrace();
//                messager.printMessage(Diagnostic.Kind.WARNING, "文件生成失败");
//            }

            /**
             * 模板二
             *
             * public class MainActivity$$$$$ARouter {
             *     public static Class findTargetClass(String path) {
             *         return path.equals("/app/MainActivity3") ? MainActivity3.class : null;
             *     }
             * }
             *
             */
            // 获取包信息
            String packageName = elementTool.getPackageOf(element).getQualifiedName().toString();

            // 获取最简单的类名 例如：MainActivity
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被@ARouter注解的类有：" + className);

            String path = element.getAnnotation(ARouter.class).path();

            // 目标：要生成的文件名称
            String findClassName = className + "$$$$$ARouter";

            // 1.方法
            MethodSpec findTargetClass = MethodSpec.methodBuilder("findTargetClass")
                    .addParameter(String.class, "path")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(Class.class)
                    .addStatement("return path.equals($S) ? $T.class : null", path, element)
                    .build();

            // 2.类
            TypeSpec targetClassName = TypeSpec.classBuilder(findClassName)
                    .addMethod(findTargetClass)
                    .addModifiers(Modifier.PUBLIC)
                    .build();

            // 3.包
            JavaFile packetf = JavaFile.builder(packageName, targetClassName).build();

            try {
                packetf.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return true;  // 不干活了  true == 干完活了
    }
}
