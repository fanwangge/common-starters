package com.hp.spiderman.annotations;

import com.hp.common.base.annotation.MethodDesc;
import com.hp.spiderman.constants.FilterLogic;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpiderNodeFilter {

    @MethodDesc("元素属性")
    String attr();

    @MethodDesc("元素属性的值")
    String val();

    @MethodDesc("逻辑运算")
    FilterLogic logic() default FilterLogic.equals;
}
