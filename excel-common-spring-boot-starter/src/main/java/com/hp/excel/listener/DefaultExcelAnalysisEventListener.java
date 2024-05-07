package com.hp.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.hp.common.base.utils.ValidateUtil;
import com.hp.excel.model.ExcelErrorMessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hp
 * @date 2022/11/7
 */
@Slf4j
public class DefaultExcelAnalysisEventListener extends ExcelAnalysisEventListener<Object,List<Object>> {

    private final List<Object> list = Lists.newArrayList();
    private final List<ExcelErrorMessageModel> errorMessageList = Lists.newArrayList();
    private long lineNum = 1L;

    @Override
    public List<Object> getData() {
        return this.list;
    }

    @Override
    public List<ExcelErrorMessageModel> getErrors() {
        return this.errorMessageList;
    }

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        Set<ConstraintViolation<Object>> violations = ValidateUtil.validate(o);
        if (!violations.isEmpty()) {
            Set<String> messageSet = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
            this.errorMessageList.add(new ExcelErrorMessageModel(this.lineNum++, messageSet));
        } else {
            this.list.add(o);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.debug("Excel read analysed");
    }
}
