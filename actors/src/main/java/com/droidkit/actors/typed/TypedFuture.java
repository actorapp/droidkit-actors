package com.droidkit.actors.typed;

import com.droidkit.actors.concurrency.Future;

/**
 * Created by ex3ndr on 14.09.14.
 */
public class TypedFuture<T> extends Future<T> {
    public void doComplete(T res) {
        onCompleted(res);
    }

    public void doError(Throwable t) {
        onError(t);
    }
}
