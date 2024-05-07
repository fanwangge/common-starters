package com.hp.excel.converter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.hp.common.base.enums.BaseEnum;
import com.hp.common.base.enums.BaseEnumBasedAdapter;

import java.util.Optional;

/**
 * 使用{@code BaseEnum.getName()}
 *
 * @author hp
 */
public abstract class AbstractBaseEnumExcelConverter<T extends Enum<T> & BaseEnum<T, E>, E> implements Converter<T>, BaseEnumBasedAdapter<T, E> {

    protected final Class<T> baseEnumType = getBaseEnumType();

    @Override
    public Class<?> supportJavaTypeKey() {
        return baseEnumType;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public T convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        final String name = cellData.getStringValue();
        return BaseEnum.parseByName(baseEnumType, name);
    }

    @Override
    public WriteCellData<String> convertToExcelData(T value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return new WriteCellData<>(Optional.ofNullable(value).map(BaseEnum::getName).orElse(StrUtil.EMPTY));
    }
}
