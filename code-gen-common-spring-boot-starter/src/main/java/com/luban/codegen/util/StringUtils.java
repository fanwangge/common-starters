package com.luban.codegen.util;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String camel(String source) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, source);
    }

    public static boolean containsNull(List<String> list) {
        List<String> nullList = list.stream().filter(Objects::isNull).collect(Collectors.toList());
        return nullList.size() > 0;
    }

    public static boolean containsNull(String... list) {
        List<String> temp = Lists.newArrayList();
        Collections.addAll(temp, list);
        List<String> nullList = temp.stream().filter(Objects::isNull).collect(Collectors.toList());
        return nullList.size() > 0;
    }

}
