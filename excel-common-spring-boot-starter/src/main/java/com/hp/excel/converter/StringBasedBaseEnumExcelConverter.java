package com.hp.excel.converter;

import com.hp.common.base.enums.BaseEnum;

/**
 * @author hp
 */
public abstract class StringBasedBaseEnumExcelConverter<T extends Enum<T> & BaseEnum<T, String>> extends AbstractBaseEnumExcelConverter<T, String> {
}
