package com.droidkit.actors.tasks.messages;

/**
 * Created by ex3ndr on 20.08.14.
 */
public class TaskTimeout {
    private final int requestId;

    public TaskTimeout(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}
