package com.example.arouter_compiler;


import com.example.arouter_annotation.ARouter;
import com.example.arouter_annotation.bean.ARouterBean;
import com.example.arouter_compiler.utils.ProcessorConfig;
import com.example.arouter_compiler.utils.ProcessorUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.lang.reflect.Type;
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
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)  // 启动服务
@SupportedAnnotationTypes({ProcessorConfig.AROUTER_PACKAGE})
@SupportedSourceVersion(SourceVersion.RELEASE_8)

@SupportedOptions({ProcessorConfig.OPTIONS, ProcessorConfig.APT_PACKAGE})  // 接收安卓工程传递的参数
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
     * $N 变量，如：pathMap
     * $L 枚举，如：TypeEnum.ACTIVITY
     */

    private Elements elementTool;
    private Types typesTool;
    private Messager messager;
    private Filer filer;

    private String options; // 各个模块传递过来的模块名 例如：app order personal
    private String aptPackage; // 各个模块传递过来的目录 用于统一存放 apt生成的文件

    // 仓库一 Path  <"路由组", List<ARouterBean>>
    private Map<String, List<ARouterBean>> pathMap = new HashMap<>();

    // 仓库二 Group <"路由组", "ARouter$$Path$$personal.class">
    private Map<String, String> groupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementTool = processingEnv.getElementUtils();
        typesTool = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();

        options = processingEnv.getOptions().get(ProcessorConfig.OPTIONS);
        aptPackage = processingEnv.getOptions().get(ProcessorConfig.APT_PACKAGE);
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>>>>>>>>>> options:" + options);
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>>>>>>>>>> aptPackage:" + aptPackage);
        if (options != null && aptPackage != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT 环境搭建完成....");
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "APT 环境有问题，请检查 options 与 aptPackage 为null...");
        }

    }

    // 如果没有在任何地方使用注解，此函数不会执行。
    /**
     * 相当于main函数  开始处理注解
     * 注解处理器的核心方法，处理具体的注解，生成java文件
     * @param annotations 使用了支持处理注解的节点集合
     * @param roundEnv  当前或是之前的运行环境,可以通过该对象查找的注解。
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>>>>>>>>>> zhaojiawei");

        if (annotations.isEmpty()) {
            return false;
        }

        // todo Call
        TypeElement callType = elementTool.getTypeElement(ProcessorConfig.CALL_API_NAME);
        TypeMirror callMirror = callType.asType();  // 自描述


        // 获取所有被 @ARouter 注解的 元素集合
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);
        messager.printMessage(Diagnostic.Kind.NOTE, "elements" + elements);

        // 通过Element工具类，获取Activity, Callback类型
        TypeElement activityType = elementTool.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE);
        // 显示类信息（获取被注解的节点，类节点）这也叫自描述 Mirror
        TypeMirror activityMirror = activityType.asType();

        // 遍历所有的类节点
        for (Element element : elements) {

            // 获取简单类名，例如：MainActivity
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被@ARetuer注解的类有：" + className); // 打印出 就证明APT没有问题

            // 拿到注解
            ARouter aRouter = element.getAnnotation(ARouter.class);

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
//            // 获取包信息
//            String packageName = elementTool.getPackageOf(element).getQualifiedName().toString();
//
//            // 获取最简单的类名 例如：MainActivity
//            String className = element.getSimpleName().toString();
//            messager.printMessage(Diagnostic.Kind.NOTE, "被@ARouter注解的类有：" + className);
//
//            String path = element.getAnnotation(ARouter.class).path();
//
//            // 目标：要生成的文件名称
//            String findClassName = className + "$$$$$ARouter";
//
//            // 1.方法
//            MethodSpec findTargetClass = MethodSpec.methodBuilder("findTargetClass")
//                    .addParameter(String.class, "path")
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(Class.class)
//                    .addStatement("return path.equals($S) ? $T.class : null", path, element)
//                    .build();
//
//            // 2.类
//            TypeSpec targetClassName = TypeSpec.classBuilder(findClassName)
//                    .addMethod(findTargetClass)
//                    .addModifiers(Modifier.PUBLIC)
//                    .build();
//
//            // 3.包
//            JavaFile packetf = JavaFile.builder(packageName, targetClassName).build();
//
//            try {
//                packetf.writeTo(filer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            // 封装路由对象
            ARouterBean aRouterBean = new ARouterBean.Builder()
                    .addGroup(aRouter.group())
                    .addPath(aRouter.path())
                    .addElement(element)
                    .build();

            // TODO  一系列的检查工作
            // ARouter注解的类 必须继承Activity
            TypeMirror typeMirror = element.asType();
            if (typesTool.isSubtype(typeMirror, activityMirror)) {
                aRouterBean.setTypeEnum(ARouterBean.TypeEnum.ACTIVITY);
            } else if (typesTool.isSubtype(typeMirror, callMirror)) {
                aRouterBean.setTypeEnum(ARouterBean.TypeEnum.CALL);
            } else {
                // 不匹配抛出异常，这里谨慎使用！考虑维护问题
                throw new RuntimeException("@ARouter注解目前仅限用于Activity类之上");
            }

            if (checkRouterPath(aRouterBean)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean Check Success:" + aRouterBean.toString());

                List<ARouterBean> routerBeans = pathMap.get(aRouterBean.getGroup());

                if (ProcessorUtils.isEmpty(routerBeans)) {
                    routerBeans = new ArrayList<>();
                    routerBeans.add(aRouterBean);
                    pathMap.put(aRouterBean.getGroup(), routerBeans);  // 加入仓库
                } else {
                    routerBeans.add(aRouterBean);
                }

            } else {
                messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            }

        } // pathMap 有值了


        // 定义生成类文件实现的接口
        TypeElement pathType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_PATH); // ARouterPath描述
        TypeElement groupType = elementTool.getTypeElement(ProcessorConfig.AROUTER_API_GROUP); // ARouterGroup描述

        messager.printMessage(Diagnostic.Kind.NOTE, "pathType===" + pathType);
        messager.printMessage(Diagnostic.Kind.NOTE, "groupType===" + groupType);

        //todo 第一大步  生成 path
        try {
            createPathFile(pathType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.ERROR, "在生成PATH模板时，异常了 e:" + e.getMessage());
        }

        //todo 第二大步 生成 group
        try {
            createGroupFile(groupType, pathType);
        } catch (IOException e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.ERROR, "在生成GROUP模板时，异常了 e:" + e.getMessage());
        }

        return true;  // 不干活了  true == 干完活了
    }

    /**
     * public class ARouter$$Group$$personal implements ARouterGroup {
     *   @Override
     *   public Map<String, Class<? extends ARouterPath>> getGroupMap() {
     *     Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
     *     groupMap.put("personal", ARouter$$Path$$personal.class);
     *     return groupMap;
     *   }
     * }
     *
     * 生成路由组Group文件，如：ARouter$$Group$$app
     * @param groupType ARouterLoadGroup接口信息
     * @param pathType  ARouterLoadPath接口信息
     * @throws IOException
     */
    private void createGroupFile(TypeElement groupType, TypeElement pathType) throws IOException {
        if (ProcessorUtils.isEmpty(groupMap) || ProcessorUtils.isEmpty(pathMap)) {
            return;
        }

        // todo ------------------生成方法返回类型----------------
        // Map<String, Class<? extends ARouterPath>>
        TypeName methodReturn = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))) // ? extends ARouterPath
        );

        // todo ------------------生成方法名----------------
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.GROUP_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(methodReturn);

        // todo ------------------生成方法体----------------
        // Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))),
                ProcessorConfig.GROUP_VAR1,
                ClassName.get(HashMap.class)
        );


        for (Map.Entry<String, String> entry : groupMap.entrySet()) {
            methodBuilder.addStatement("$N.put($S, $T.class)",
                    ProcessorConfig.GROUP_VAR1,
                    entry.getKey(),
                    ClassName.get(aptPackage, entry.getValue()));
        }

        methodBuilder.addStatement("return $N", ProcessorConfig.GROUP_VAR1);

        String finalClassName = ProcessorConfig.GROUP_FILE_NAME + options;

        messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由组Group类文件：" +
                aptPackage + "." + finalClassName);

        // 生成java文件
        JavaFile.builder(aptPackage,
                TypeSpec.classBuilder(finalClassName)
                        .addSuperinterface(ClassName.get(groupType))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodBuilder.build())
                        .build())
                .build()
                .writeTo(filer);
    }


    /**
     * public class ARouter$$Path$$personal implements ARouterPath {
     *   @Override
     *   public Map<String, RouterBean> getPathMap() {
     *     Map<String, RouterBean> pathMap = new HashMap<>();
     *     pathMap.put("/personal/Personal_Main2Activity", RouterBean.create(ARouterBean.TypeEnum.ACTIVITY,
     *     MainActivity.class, ""));
     *     pathMap.put("/personal/Personal_MainActivity", RouterBean.create());
     *     return pathMap;
     *   }
     * }
     */
    private void createPathFile(TypeElement pathType) throws IOException {
        // 判断 map 仓库中 是否有需要生成的文件
        if (ProcessorUtils.isEmpty(pathMap)) {
            return;
        }

        // todo ------------------生成方法返回类型----------------
        // Map<String, ARouterBean>
        TypeName methodReturn = ParameterizedTypeName.get(
               ClassName.get(Map.class),
               ClassName.get(String.class),
               ClassName.get(ARouterBean.class)
        );

        // 遍历仓库 app,order,personal
        for (Map.Entry<String, List<ARouterBean>> entry : pathMap.entrySet()) {
            // 1.方法

            // todo ------------------生成方法名----------------
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(ProcessorConfig.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturn);

            // todo ------------------生成方法体----------------
            methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                            ClassName.get(Map.class),
                            ClassName.get(String.class),
                            ClassName.get(ARouterBean.class),
                            ProcessorConfig.PATH_VAR1,
                            ClassName.get(HashMap.class)
                            );

            List<ARouterBean> pathList = entry.getValue();
            for (ARouterBean aRouterBean: pathList) {
                methodBuilder.addStatement("$N.put($S, $T.create($T.$L, $T.class, $S, $S))",
                        ProcessorConfig.PATH_VAR1,
                        aRouterBean.getPath(),
                        ClassName.get(ARouterBean.class),
                        ClassName.get(ARouterBean.TypeEnum.class),
                        aRouterBean.getTypeEnum(),
                        ClassName.get((TypeElement) aRouterBean.getElement()),
                        aRouterBean.getPath(),
                        aRouterBean.getGroup());
            }

            methodBuilder.addStatement("return $N", ProcessorConfig.PATH_VAR1);

            // todo  注意： 如果类有实现 implements，则方法和类需要合为一体生成，这是特殊情况
            String finalClassName = ProcessorConfig.PATH_FILE_NAME + entry.getKey();

            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由path类文件：" + aptPackage + "." + finalClassName);

            // 生成java文件
            JavaFile.builder(aptPackage,
                    TypeSpec.classBuilder(finalClassName)
                            .addSuperinterface(ClassName.get(pathType))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())
                            .build())
                    .build()
                    .writeTo(filer);

            groupMap.put(entry.getKey(), finalClassName);

        }

    }

    /**
     * 校验@ARouter注解的值，如果group未填写就从必填项path中截取数据
     * @param routerBean 路由详细信息，最终实体封装类
     */
    private boolean checkRouterPath(ARouterBean routerBean) {
        String path = routerBean.getPath();
        String group = routerBean.getGroup();

        // 校验
        // @ARouter注解中的path值，必须要以 / 开头（模仿阿里ARouter规范）
        if (ProcessorUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的path值，必须要以 / 开头");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));

        // @ARouter注解中的group有赋值情况
        if (!ProcessorUtils.isEmpty(group) && !group.equals(options)) {
            // 架构师定义规范，让开发者遵循
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的group值必须和子模块名一致！");
            return false;
        } else {
            routerBean.setGroup(finalGroup);
        }

        return true;
    }


}
