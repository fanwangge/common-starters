package com.hp.excel.exception;

import java.io.Serial;

/**
 * @author hp
 */
public class ExcelExportException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -8788505401952773283L;

    public ExcelExportException() {
    }

    public ExcelExportException(String message) {
        super(message);
    }

    public ExcelExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelExportException(Throwable cause) {
        super(cause);
    }

    public ExcelExportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
