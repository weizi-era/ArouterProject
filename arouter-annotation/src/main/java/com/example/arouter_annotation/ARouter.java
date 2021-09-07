package com.example.arouter_annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Target(TYPE)  // 类上 接口上
@Retention(CLASS) // 编译期
public @interface ARouter {

    String path();

    String group() default "";
}
