package com.hp.excel.enhance;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.head.HeadGenerator;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;

/**
 * @author hp
 */
public interface ExcelWriterBuilderEnhance {

    ExcelWriterBuilder enhanceExcel(
            ExcelWriterBuilder writerBuilder,
            HttpServletResponse response,
            ResponseExcel responseExcel,
            Collection<? extends Class<?>> dataClasses,
            String templatePath
    );

    ExcelWriterSheetBuilder enhanceSheet(
            ExcelWriterSheetBuilder writerSheetBuilder,
            Integer sheetNo,
            String sheetName,
            Class<?> dataClass,
            String template, Class<? extends HeadGenerator> headEnhancerClass
    );
}
