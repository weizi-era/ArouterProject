# ArouterProject
this is ARouter project

运行期框架(注解 反射) ：XUtils  
编译器框架(APT技术) ：Dagger2 Room ARouter DataBinding Butterknife

ARouter 组件化路由

通过注解处理器 使用JavaPoet自动生成java文件

JavaPoet是一种OOP思想，面向对象编程，一切以对象为中心

倒序生成

1.方法

2.类

3.包

TypeSpec  生成类

MethodSpec  生成方法

ParameterSpec  生成参数

AnnotationSpec 生成注解

JavaFile  生成java文件

最终生成的Group文件：

public class ARouter$$Group$$personal implements ARouterGroup {
  @Override
  public Map<String, Class<? extends ARouterPath>> getGroupMap() {
    Map<String, Class<? extends ARouterPath>> groupMap = new HashMap<>();
    groupMap.put("personal", ARouter$$Path$$personal.class);
    return groupMap;
  }
}

最终生成的Path文件：

public class ARouter$$Path$$personal implements ARouterPath {
  @Override
  public Map<String, RouterBean> getPathMap() {
    Map<String, RouterBean> pathMap = new HashMap<>();
    pathMap.put("/personal/Personal_Main2Activity", RouterBean.create();
    pathMap.put("/personal/Personal_MainActivity", RouterBean.create());
    return pathMap;
  }
}
