package com.hp.excel.enhance.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.hp.excel.constant.ExcelConstants;
import com.hp.excel.model.AbstractExcelSelectModel;
import com.hp.excel.model.ExcelCascadeModel;
import com.hp.excel.model.ExcelSelectModel;
import com.hp.excel.util.ExcelHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hp
 */
@Slf4j
public class SelectDataSheetWriteHandler implements SheetWriteHandler {

    protected final AtomicInteger selectionColumnIndex;
    protected final Map<Integer, ? extends AbstractExcelSelectModel<?>> selectionMapping;
    public SelectDataSheetWriteHandler(
            AtomicInteger selectionColumnIndex,
            Map<Integer, ? extends AbstractExcelSelectModel<?>> selectionMapping
    ) {
        this.selectionColumnIndex = selectionColumnIndex;
        this.selectionMapping = selectionMapping;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        final Workbook workbook = writeWorkbookHolder.getWorkbook();
        final Sheet sheet = writeSheetHolder.getSheet();
        selectionMapping.forEach((colIndex, model) -> {
            if (model.hasParentColumn()) {
                ExcelHelper.addCascadeDropdownToSheet(
                        workbook,
                        sheet,
                        writeWorkbookHolder.getWorkbook().getSheet(ExcelConstants.SELECTION_HOLDER_SHEET_NAME),
                        selectionColumnIndex,
                        (ExcelCascadeModel) model
                );
            } else {
                ExcelHelper.addDropdownToSheet(
                        sheet,
                        writeWorkbookHolder.getWorkbook().getSheet(ExcelConstants.SELECTION_HOLDER_SHEET_NAME),
                        selectionColumnIndex,
                        (ExcelSelectModel) model
                );
            }
        });
    }
}
