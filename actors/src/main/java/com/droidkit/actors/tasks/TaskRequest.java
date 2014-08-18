package com.droidkit.actors.tasks;

import com.droidkit.actors.ActorRef;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class TaskRequest {
    private final int requestId;
    private final ActorRef ref;

    public TaskRequest(int requestId, ActorRef ref) {
        this.requestId = requestId;
        this.ref = ref;
    }

    public int getRequestId() {
        return requestId;
    }

    public ActorRef getRef() {
        return ref;
    }
}
