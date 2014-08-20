package com.droidkit.actors.tasks;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.messages.DeadLetter;

import java.util.HashMap;

/**
 * Created by ex3ndr on 20.08.14.
 */
public class ActorAskImpl {

    private HashMap<Integer, AskContainer> asks = new HashMap<Integer, AskContainer>();
    private int nextReqId = 1;
    private ActorRef self;

    public ActorAskImpl(ActorRef self) {
        this.self = self;
    }

    public <T> void ask(ActorRef ref, AskCallback<T> callback) {
        int reqId = nextReqId++;
        AskContainer container = new AskContainer(callback, ref, reqId);
        asks.put(reqId, container);
        ref.send(new TaskRequest(reqId, self));
    }

    public boolean onTaskResult(TaskResult result) {
        AskContainer container = asks.remove(result.getRequestId());
        if (container != null) {
            container.callback.onResult(result.getRes());
            return true;
        }

        return false;
    }

    public boolean onDeadLetter(DeadLetter letter) {
        if (letter.getMessage() instanceof TaskRequest) {
            TaskRequest request = (TaskRequest) letter.getMessage();
            AskContainer container = asks.remove(request.getRequestId());
            if (container != null) {
                container.callback.onError(null);
                return true;
            }
        }

        return false;
    }

    private class AskContainer {
        public final AskCallback callback;
        public final ActorRef ref;
        public final int requestId;

        private AskContainer(AskCallback callback, ActorRef ref, int requestId) {
            this.callback = callback;
            this.ref = ref;
            this.requestId = requestId;
        }
    }
}
