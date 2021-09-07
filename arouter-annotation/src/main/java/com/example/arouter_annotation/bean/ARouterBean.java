package com.example.arouter_annotation.bean;

import javax.lang.model.element.Element;

public class ARouterBean {

    public enum TypeEnum {
        ACTIVITY
    }

    private TypeEnum typeEnum;  // 枚举类型：activity
    private Element element;  // 类节点
    private Class<?> aClass;  // 注解使用的类对象
    private String path; // 路由地址
    private String group; // 路由组

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "ARouterBean{" +
                "typeEnum=" + typeEnum +
                ", element=" + element +
                ", aClass=" + aClass +
                ", path='" + path + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
