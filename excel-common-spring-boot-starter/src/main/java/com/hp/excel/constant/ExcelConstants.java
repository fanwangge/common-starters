package com.hp.excel.constant;

import com.hp.common.base.annotation.FieldDesc;

/**
 * @author hp
 */
public interface ExcelConstants {

    @FieldDesc("request.attribute中持有该变量时,作为自定义文件名称,动态名称")
    String FILENAME_ATTRIBUTE_KEY = "__EXCEL_NAME_KEY__";

    @FieldDesc("名称管理器的引用格式")
    String NAME_MANAGER_FORMULA_FORMAT = "%s!$%s$%s:$%s$%s";

    @FieldDesc("下拉列表的引用格式")
    String DROPDOWN_FORMULA_FORMAT = "=%s!$%s$%s:$%s$%s";

    @FieldDesc("indirect引用函数的格式,下划线用于拼接特殊名称管理名称")
    String INDIRECT_FORMULA_FORMAT = "INDIRECT(CONCATENATE(\"_\",$%s%s))";

    String SELECTION_HOLDER_SHEET_NAME = "_selectionHolderSheet";

    enum MergeStrategy {
        CONTENT,
    }

}
