package com.hp.excel.enhance;

import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.hp.excel.annotation.RequestExcel;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author hp
 * @date 2022/11/7
 */
public interface ExcelReaderBuilderEnhance {

    ExcelReaderBuilder enhanceExcel(ExcelReaderBuilder writerBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass);

    ExcelReaderSheetBuilder enhanceSheet(ExcelReaderSheetBuilder writerSheetBuilder, HttpServletRequest request, RequestExcel requestExcel, Class<?> dataClass);
}
