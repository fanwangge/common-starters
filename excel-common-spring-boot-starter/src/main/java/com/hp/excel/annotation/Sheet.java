package com.hp.excel.annotation;


import com.hp.excel.head.HeadGenerator;

import java.lang.annotation.*;

/**
 * sheet名称*
 *
 * @author hp
 * @date 2022/11/7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sheet {

    int sheetNo() default -1;

    String sheetName();

    String template() default "";

    String[] includes() default {};

    String[] excludes() default {};

    Class<? extends HeadGenerator> headGenerator() default HeadGenerator.class;
}
