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

}
