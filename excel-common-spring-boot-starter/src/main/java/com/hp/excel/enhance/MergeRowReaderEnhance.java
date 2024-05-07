package com.hp.excel.enhance;

import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.hp.excel.annotation.RequestExcel;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author hp
 */
public class MergeRowReaderEnhance implements ExcelReaderBuilderEnhance {
    @Override
    public ExcelReaderBuilder enhanceExcel(
            ExcelReaderBuilder writerBuilder,
            HttpServletRequest request,
            RequestExcel requestExcel,
            Class<?> dataClass
    ) {
        return writerBuilder.extraRead(CellExtraTypeEnum.MERGE);
    }

    @Override
    public ExcelReaderSheetBuilder enhanceSheet(
            ExcelReaderSheetBuilder writerSheetBuilder,
            HttpServletRequest request,
            RequestExcel requestExcel,
            Class<?> dataClass
    ) {
        return writerSheetBuilder;
    }
}
