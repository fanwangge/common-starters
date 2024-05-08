package com.hp.excel.handler;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.annotation.Sheet;
import com.hp.excel.exception.ExcelExportException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.util.List;

/**
 * Export single sheet if the controller method
 * returns {@code List<T>}
 *
 * @author hp
 */
public class SingleSheetWriteHandler extends AbstractExcelSheetWriteHandler {

    public SingleSheetWriteHandler(ObjectProvider<List<Converter<?>>> converterProvider) {
        super(converterProvider);
    }

    @Override
    public boolean support(Object obj) {
        if (!(obj instanceof List<?> list)) {
            throw new IllegalArgumentException("@ResponseExcel annotation works only when the return type is a List");
        }
        return CollUtil.isNotEmpty(list) && !(list.get(0) instanceof List);
    }

    @Override
    public void write(Object o, HttpServletRequest request, HttpServletResponse response, ResponseExcel responseExcel) {
        final List<?> data = (List<?>) o;
        final List<? extends Class<?>> dataClasses = data.stream().map(Object::getClass).distinct().toList();

        try (final ExcelWriter excelWriter = this.getExcelWriter(responseExcel, dataClasses, request, response)) {

            final Sheet sheetAnn = responseExcel.sheets()[0];
            final WriteSheet sheet = this.getWriteSheet(
                    sheetAnn,
                    dataClasses.getFirst(),
                    responseExcel.template(),
                    responseExcel.headGenerator()
            );

            if (responseExcel.fill()) {
                excelWriter.fill(data, sheet);
            } else {
                excelWriter.write(data, sheet);
            }

            excelWriter.finish();
        } catch (IOException e) {
            throw new ExcelExportException(e);
        }
    }
}
