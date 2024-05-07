package com.hp.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime转换器*
 * @author hp
 * @date 2022/11/7
 */
@Getter
@AllArgsConstructor
public enum LocalDateTimeConverter implements Converter<LocalDateTime> {
    INSTANCE;

    private static final String DASH = "-";

    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDateTime convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        final String stringValue = cellData.getStringValue();
        String format;
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            format = contentProperty.getDateTimeFormatProperty().getFormat();
        } else {
            format = findWithLength(stringValue);
        }
        return LocalDateTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(format));
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String format;
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            format = contentProperty.getDateTimeFormatProperty().getFormat();
        } else {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new WriteCellData<>(value.format(DateTimeFormatter.ofPattern(format)));
    }

    private static String findWithLength(String dateString) {
        int length = dateString.length();
        return switch (length) {
            case 10 -> "yyyy-MM-dd";
            case 11, 12, 13, 15, 16, 18, 14 -> "yyyyMMddHHmmss";
            case 17 -> "yyyyMMdd HH:mm:ss";
            case 19 -> dateString.contains(DASH) ? "yyyy-MM-dd HH:mm:ss" : "yyyy/MM/dd HH:mm:ss";
            default -> throw new IllegalArgumentException("can not find date format for：" + dateString);
        };
    }
}
