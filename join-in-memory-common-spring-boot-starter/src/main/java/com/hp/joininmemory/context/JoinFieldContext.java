package com.hp.joininmemory.context;

import com.hp.joininmemory.support.AbstractJoinFieldV2Executor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @author hp
 */
@Getter
@Setter
public class JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> {

    private final AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> executor;

    private SOURCE_DATA sourceData;

    private JOIN_KEY joinKey;

    private List<JOIN_DATA> joinData;

    private JOIN_RESULT joinResult;

    public JoinFieldContext(
            AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> executor,
            SOURCE_DATA sourceData
    ) {
        this.sourceData = sourceData;
        this.executor = executor;
    }

    public boolean notEmptyJoinKey() {
        return Objects.nonNull(joinKey);
    }
}
