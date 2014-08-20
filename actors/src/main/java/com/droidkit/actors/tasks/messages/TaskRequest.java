package com.droidkit.actors.tasks.messages;

import com.droidkit.actors.ActorRef;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class TaskRequest {
    private final int requestId;

    public TaskRequest(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }
}
