package org.cai.neo4j.algo;

import java.util.Arrays;

/**
 * @author cai584770
 * @date 2024/7/2 15:14
 * @Version
 */
public interface IdFunction {
    long of(String variable);

    default long[] of(String... variables) {
        return Arrays.stream(variables).mapToLong(this::of).toArray();
    }
}
