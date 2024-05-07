package com.hp.excel.model;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author hp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelErrorMessageModel {

    private Long lineNum;
    private Set<String> errors = Sets.newHashSet();

    public ExcelErrorMessageModel(Set<String> errors) {
        this.errors = errors;
    }

    public ExcelErrorMessageModel(String error) {
        this.errors = Sets.newHashSet(error);
    }
}
