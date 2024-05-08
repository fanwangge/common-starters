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
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.util.List;

/**
 * Export multiple sheets if the controller
 * method returns {@code List<List<T>>}.
 *
 * @author hp
 */
public class MultiSheetWriteHandler extends AbstractExcelSheetWriteHandler {

    public MultiSheetWriteHandler(ObjectProvider<List<Converter<?>>> converterProvider) {
        super(converterProvider);
    }

    @Override
    public boolean support(Object obj) {
        if (!(obj instanceof List<?> list)) {
            throw new IllegalArgumentException("@ResponseExcel annotation works only when the return type is a List");
        }
        return CollUtil.isNotEmpty(list) && (list.get(0) instanceof List);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(Object o, HttpServletRequest request, HttpServletResponse response, ResponseExcel responseExcel) {
        final List<List<?>> list = (List<List<?>>) o;
        final Sheet[] sheets = responseExcel.sheets();
        final List<Class<?>> dataClasses = Lists.newArrayList();
        for (int i = 0; i < sheets.length; ++i) {
            dataClasses.add(list.get(i).getFirst().getClass());
        }

        try (final ExcelWriter excelWriter = getExcelWriter(responseExcel, dataClasses, request, response)) {

            for (int i = 0; i < sheets.length; ++i) {
                final List<?> data = list.get(i);
                final Sheet sheetAnn = sheets[i];
                final WriteSheet sheet = this.getWriteSheet(
                        sheetAnn,
                        data.getFirst().getClass(),
                        sheetAnn.template(),
                        sheetAnn.headGenerator()
                );

                if (responseExcel.fill()) {
                    excelWriter.fill(data, sheet);
                } else {
                    excelWriter.write(data, sheet);
                }
            }

            excelWriter.finish();
        } catch (IOException e) {
            throw new ExcelExportException(e);
        }
    }
}
