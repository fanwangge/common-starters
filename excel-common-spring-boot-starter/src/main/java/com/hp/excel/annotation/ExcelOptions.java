package com.hp.excel.annotation;

import com.hp.common.base.annotation.MethodDesc;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface ExcelOptions {

    @MethodDesc("动态数据, 通过SpEL表达式加载")
    @Language("SpEL")
    String expression() default "";

//    @MethodDesc("动态数据参数, 通过SpEL表达式加载,并提供给 expression 使用, 该表达式不接受任何参数")
//    @Language("SpEL")
//    String parameters() default "";

}
