package com.hp.excel.enhance.handler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.google.common.collect.Maps;
import com.hp.excel.annotation.ExcelMerge;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hp
 * @date 2022/11/8
 */
@Slf4j
public abstract class AbstractExcelMergeRowWriteHandler implements RowWriteHandler {

    protected final Map<Integer, ExcelMerge> excelMergeMapping;

    public AbstractExcelMergeRowWriteHandler(Class<?> dataClass) {
        this.excelMergeMapping = this.createExcelMergeMapping(dataClass);
    }

    protected Map<Integer, ExcelMerge> createExcelMergeMapping(Class<?> dataClass) {
        final Map<Integer, ExcelMerge> mergeHolder = Maps.newHashMap();
        final List<Field> fieldHolder = Lists.newArrayList();
        for (Class<?> acls = dataClass; acls != null; acls = acls.getSuperclass()) {
            final List<Field> fields = Arrays.stream(acls.getDeclaredFields())
                    .peek(f -> {
                        if (!Modifier.isPublic(f.getModifiers())) {
                            f.setAccessible(true);
                        }
                    })
                    .filter(f -> f.isAnnotationPresent(ExcelProperty.class))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(fields)) {
                fieldHolder.addAll(fields);
            }
        }
        if (CollUtil.isNotEmpty(fieldHolder)) {
            for (int i = 0; i < fieldHolder.size(); i++) {
                final Field field = fieldHolder.get(i);
                final ExcelMerge excelMerge = field.getAnnotation(ExcelMerge.class);
                if (excelMerge != null) {
                    final ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                    if (excelProperty == null) {
                        throw new UnsupportedOperationException(" @ExcelMerge works only when @ExcelProperty is set ");
                    }
                    final int index = excelProperty.index() != -1 ? excelProperty.index() : i;
                    mergeHolder.put(index, excelMerge);
                }
            }
        }
        return mergeHolder;
    }

    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        if (context.getHead() || context.getRelativeRowIndex() == null) {
            return;
        }
        this.merge(excelMergeMapping, context);
    }

    protected abstract void merge(Map<Integer, ExcelMerge> mergeHolder, RowWriteHandlerContext context);
}
