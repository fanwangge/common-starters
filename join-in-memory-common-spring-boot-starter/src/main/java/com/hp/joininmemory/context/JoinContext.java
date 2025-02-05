package com.hp.joininmemory.context;

import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@JoinInMemoryConfig
public class JoinContext<DATA> {

    private final Class<DATA> dataClass;

    private final JoinInMemoryConfig config;

    public JoinContext(Class<DATA> dataClass, JoinInMemoryConfig config) {
        this.dataClass = dataClass;
        this.config = Optional.ofNullable(config).orElse(this.getClass().getAnnotation(JoinInMemoryConfig.class));
    }
}
