package com.droidkit.actors;

/**
 * Created by ex3ndr on 14.08.14.
 */
public class ActorContext {
    private final ActorRef self;
    private final ActorSystem system;

    public ActorContext(ActorRef self, ActorSystem system) {
        this.self = self;
        this.system = system;
    }

    public ActorRef getSelf() {
        return self;
    }

    public ActorSystem getSystem() {
        return system;
    }
}
