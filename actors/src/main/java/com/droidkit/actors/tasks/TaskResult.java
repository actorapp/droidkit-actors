package com.droidkit.actors.tasks;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class TaskResult<T> {
    private final int requestId;
    private final T res;

    public TaskResult(int requestId, T res) {
        this.requestId = requestId;
        this.res = res;
    }

    public T getRes() {
        return res;
    }

    public int getRequestId() {
        return requestId;
    }
}
