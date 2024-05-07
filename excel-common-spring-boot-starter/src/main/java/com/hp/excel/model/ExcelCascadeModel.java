package com.hp.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.hp.excel.annotation.ExcelSelect;
import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @author hp
 * @date 2022/11/7
 */
@EqualsAndHashCode(callSuper = false)
public class ExcelCascadeModel extends AbstractExcelSelectModel<Map<Object, Collection<Object>>> {

    public ExcelCascadeModel(@NotNull Field field, @NotNull ExcelSelect excelSelect, @Nullable ExcelProperty excelProperty, int defaultSort) {
        super(field, excelSelect, excelProperty, defaultSort);
    }
}
