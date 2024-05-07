package com.hp.excel.annotation;


import java.lang.annotation.*;

import static com.hp.excel.constant.ExcelConstants.MergeStrategy;

/**
 * 暂时简单点，默认使用了此注解的列都是一组，做一样的操作
 * 只有使用注解列且，所有注解列同上一行每列数据相同时，会记录或发生合并
 * 所有使用该注解列的值必须和下行相同，否则不合并
 *
 * @author hp
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelMerge {

    MergeStrategy rowStrategy() default MergeStrategy.CONTENT;

}
