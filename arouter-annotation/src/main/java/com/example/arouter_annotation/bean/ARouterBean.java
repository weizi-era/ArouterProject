package com.example.arouter_annotation.bean;

import javax.lang.model.element.Element;

public class ARouterBean {

    public enum TypeEnum {
        ACTIVITY,
        CALL
    }

    private TypeEnum typeEnum;  // 枚举类型：activity
    private Element element;  // 类节点
    private Class<?> aClass;  // 注解使用的类对象
    private String path; // 路由地址
    private String group; // 路由组

    private ARouterBean(TypeEnum typeEnum, Class<?> aClass, String path, String group) {
        this.typeEnum = typeEnum;
        this.aClass = aClass;
        this.path = path;
        this.group = group;
    }

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

    // 对外暴露  构造方法
    public static ARouterBean create(TypeEnum type, Class<?> clazz, String path, String group) {
        return new ARouterBean(type, clazz, path, group);
    }

    // 构建者模式相关
    private ARouterBean(Builder builder) {
        this.typeEnum = builder.typeEnum;
        this.element = builder.element;
        this.aClass = builder.aClass;
        this.path = builder.path;
        this.group = builder.group;
    }

    /**
     * 构建者模式
     */
    public static class Builder {

        private TypeEnum typeEnum;  // 枚举类型：activity
        private Element element;  // 类节点
        private Class<?> aClass;  // 注解使用的类对象
        private String path; // 路由地址
        private String group; // 路由组

        public Builder addType(TypeEnum typeEnum) {
            this.typeEnum = typeEnum;
            return this;
        }

        public Builder addElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder addClass(Class<?> aClass) {
            this.aClass = aClass;
            return this;
        }

        public Builder addPath(String path) {
            this.path = path;
            return this;
        }

        public Builder addGroup(String group) {
            this.group = group;
            return this;
        }

        public ARouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path必填项为空，如：/app/MainActivity");
            }
            return new ARouterBean(this);
        }
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
