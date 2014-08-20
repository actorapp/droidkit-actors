package com.droidkit.actors.tasks;

/**
 * Created by ex3ndr on 20.08.14.
 */
public class TaskError {
    private final int requestId;
    private final Throwable throwable;

    public TaskError(int requestId, Throwable throwable) {
        this.requestId = requestId;
        this.throwable = throwable;
    }

    public int getRequestId() {
        return requestId;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
