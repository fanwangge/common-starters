package com.hp.excel.converter;

import com.hp.common.base.enums.BaseEnum;

/**
 * @author hp
 */
public abstract class IntegerBasedBaseEnumExcelConverter<T extends Enum<T> & BaseEnum<T, Integer>> extends AbstractBaseEnumExcelConverter<T, Integer> {
}
