package com.droidkit.actors;

/**
 * Context of actor
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class ActorContext {
    private final ActorRef self;
    private final ActorSystem system;

    /**
     * INTERNAL API
     * <p/>
     * Creating of actor context
     *
     * @param self   actor reference
     * @param system actor system
     */
    public ActorContext(ActorRef self, ActorSystem system) {
        this.self = self;
        this.system = system;
    }

    /**
     * Actor Reference
     *
     * @return reference
     */
    public ActorRef getSelf() {
        return self;
    }

    /**
     * Actor system
     *
     * @return Actor system
     */
    public ActorSystem getSystem() {
        return system;
    }
}
