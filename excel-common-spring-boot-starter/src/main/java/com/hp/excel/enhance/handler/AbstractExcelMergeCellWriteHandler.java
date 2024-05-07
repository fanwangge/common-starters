package com.hp.excel.enhance.handler;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import org.apache.poi.ss.usermodel.Cell;

/**
 * TODO cell级别的合并*
 *
 * @author hp
 */
public abstract class AbstractExcelMergeCellWriteHandler implements CellWriteHandler {
    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        final Cell cell = context.getCell();
    }
}
