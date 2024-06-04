
package com.hp.joininmemory.exception;

import java.util.function.BiConsumer;

/**
 * @author hp
 */
@FunctionalInterface
public interface ExceptionNotifier {

    BiConsumer<Object,Throwable> handle();
}
