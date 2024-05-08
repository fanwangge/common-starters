package com.hp.excel.handler;


import com.hp.excel.annotation.ResponseExcel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author hp
 */
public interface ExcelSheetWriteHandler {

    boolean support(Object object);

    void check(ResponseExcel responseExcel);

    void export(Object object, HttpServletRequest request, HttpServletResponse response, ResponseExcel responseExcel) throws Exception;

    void write(Object object, HttpServletRequest request, HttpServletResponse response, ResponseExcel responseExcel);

}
