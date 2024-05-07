package com.hp.excel.enhance.handler;

import com.alibaba.excel.write.handler.WorkbookWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.hp.excel.constant.ExcelConstants;
import com.hp.excel.util.ExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author hp
 */
@Slf4j
public class SelectDataWorkbookWriteHandler implements WorkbookWriteHandler {

    @Override
    public void afterWorkbookCreate(WriteWorkbookHolder writeWorkbookHolder) {
        final Sheet sheet = ExcelHelper.createSheet(writeWorkbookHolder.getWorkbook(), ExcelConstants.SELECTION_HOLDER_SHEET_NAME, true);
        // TODO
//        ExcelHelper.hideSheet(writeWorkbookHolder.getWorkbook(), sheet);
    }
}
