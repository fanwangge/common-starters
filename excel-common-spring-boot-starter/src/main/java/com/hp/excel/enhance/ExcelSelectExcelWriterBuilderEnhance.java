package com.hp.excel.enhance;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.google.common.collect.Maps;
import com.hp.excel.annotation.ExcelSelect;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.enhance.handler.SelectDataSheetWriteHandler;
import com.hp.excel.enhance.handler.SelectDataWorkbookWriteHandler;
import com.hp.excel.head.HeadGenerator;
import com.hp.excel.model.AbstractExcelSelectModel;
import com.hp.excel.model.ExcelCascadeModel;
import com.hp.excel.model.ExcelSelectModel;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hp
 */

@Slf4j
public class ExcelSelectExcelWriterBuilderEnhance implements ExcelWriterBuilderEnhance {

    protected final AtomicInteger selectionColumnIndex = new AtomicInteger(0);
    protected Map<Class<?>, Map<Integer, ? extends AbstractExcelSelectModel<?>>> selectionMapMapping = Maps.newHashMap();

    @Override
    public ExcelWriterBuilder enhanceExcel(ExcelWriterBuilder writerBuilder, HttpServletResponse response, ResponseExcel responseExcel, Collection<? extends Class<?>> dataClasses, String templatePath) {
        dataClasses.forEach(dataClass -> selectionMapMapping.put(dataClass, createSelectionMapping(dataClass)));
        return writerBuilder.registerWriteHandler(new SelectDataWorkbookWriteHandler());
    }

    @Override
    public ExcelWriterSheetBuilder enhanceSheet(ExcelWriterSheetBuilder writerSheetBuilder, Integer sheetNo, String sheetName, Class<?> dataClass, String template, Class<? extends HeadGenerator> headEnhancerClass) {
        if (selectionMapMapping.containsKey(dataClass)) {
            final Map<Integer, ? extends AbstractExcelSelectModel<?>> selectionMapping = selectionMapMapping.get(dataClass);
            writerSheetBuilder.registerWriteHandler(new SelectDataSheetWriteHandler(selectionColumnIndex, selectionMapping));
        }
        return writerSheetBuilder;
    }

    @Nullable
    private static <T> Map<Integer, ? extends AbstractExcelSelectModel<?>> createSelectionMapping(@Nonnull Class<T> dataClass) {
        final Field[] fields = ReflectUtil.getFields(dataClass);
        final AtomicInteger fieldIndex = new AtomicInteger(1);
        final Map<Integer, ? extends AbstractExcelSelectModel<?>> selectionMapping = Arrays.stream(fields)
                .map(field -> {
                    final ExcelSelect excelSelect = AnnotatedElementUtils.getMergedAnnotation(field, ExcelSelect.class);
                    if (Objects.isNull(excelSelect)) {
                        log.debug("No ExcelSelect annotated on {}, skip processing", field.getName());
                        return null;
                    }
                    final ExcelProperty excelProperty = AnnotatedElementUtils.getMergedAnnotation(field, ExcelProperty.class);
                    AbstractExcelSelectModel<?> excelSelectModel;
                    if (StrUtil.isNotEmpty(excelSelect.parentColumnName())) {
                        excelSelectModel = new ExcelCascadeModel(field, excelSelect, excelProperty, fieldIndex.getAndIncrement());
                    } else {
                        excelSelectModel = new ExcelSelectModel(field, excelSelect, excelProperty, fieldIndex.getAndIncrement());
                    }
                    return excelSelectModel;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(AbstractExcelSelectModel::getColumnIndex, Function.identity(), (a, b) -> a));

        if (MapUtil.isEmpty(selectionMapping)) {
            return null;
        }

        // 设置父列索引
        final Map<String, Integer> columnNamedMapping = selectionMapping.values()
                .stream()
                .collect(Collectors.toMap(AbstractExcelSelectModel::getColumnName, AbstractExcelSelectModel::getColumnIndex));
        selectionMapping.forEach((k, v) -> {
            if (v.hasParentColumn() && columnNamedMapping.containsKey(v.getParentColumnName())) {
                v.setParentColumnIndex(columnNamedMapping.get(v.getParentColumnName()));
            }
        });

        return selectionMapping;
    }
}
