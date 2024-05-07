package com.hp.excel.handler;


import com.hp.excel.annotation.ResponseExcel;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author hp
 */
public interface ExcelSheetWriteHandler {

    boolean support(Object obj);

    void check(ResponseExcel responseExcel);

    void export(Object o, HttpServletResponse response, ResponseExcel responseExcel) throws Exception;

    void write(Object o, HttpServletResponse response, ResponseExcel responseExcel);
}
