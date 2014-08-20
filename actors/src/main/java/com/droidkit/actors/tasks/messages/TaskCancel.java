package com.droidkit.actors.tasks.messages;

/**
 * Created by ex3ndr on 20.08.14.
 */
public class TaskCancel {
    private int requestId;

    public TaskCancel(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}
