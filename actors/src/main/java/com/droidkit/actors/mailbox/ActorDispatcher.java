package com.droidkit.actors.mailbox;

import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.dispatch.Dispatch;
import com.droidkit.actors.dispatch.ThreadPoolDispatcher;

/**
 * Basic ActorDispatcher backed by ThreadPoolDispatcher
 */
public class ActorDispatcher extends AbsActorDispatcher {
    public ActorDispatcher(String name, ActorSystem actorSystem, int threadsCount) {
        this(name, actorSystem, threadsCount, Thread.MIN_PRIORITY);
    }

    public ActorDispatcher(String name, ActorSystem actorSystem, int threadsCount, int priority) {
        super(name, actorSystem);
        initDispatcher(new ThreadPoolDispatcher<Envelope, MailboxesQueue>(threadsCount, priority, new MailboxesQueue(),
                new Dispatch<Envelope>() {
                    @Override
                    public void dispatchMessage(Envelope message) {
                        processEnvelope(message);
                    }
                }, true));
    }
}
