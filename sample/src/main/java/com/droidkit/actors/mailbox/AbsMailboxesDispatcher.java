package com.droidkit.actors.mailbox;

import com.droidkit.actors.ActorScope;
import com.droidkit.actors.dispatch.AbstractDispatchQueue;
import com.droidkit.actors.dispatch.AbstractDispatcher;
import com.droidkit.actors.messages.PoisonPill;
import com.droidkit.actors.messages.StartActor;

/**
 * Created by ex3ndr on 14.08.14.
 */
public abstract class AbsMailboxesDispatcher extends AbstractDispatcher<Envelope> {

    protected AbsMailboxesDispatcher() {
    }

    protected AbsMailboxesDispatcher(int priority) {
        super(priority);
    }

    protected AbsMailboxesDispatcher(AbstractDispatchQueue<Envelope> queue) {
        super(queue);
    }

    protected AbsMailboxesDispatcher(int priority, AbstractDispatchQueue<Envelope> queue) {
        super(priority, queue);
    }

    public abstract void connectScope(ActorScope actor);

    protected final void processEnvelope(Envelope envelope, ActorScope actor) {
        if (actor == null) {
            //TODO: add logging
            return;
        }

        if (envelope.getMessage() == StartActor.INSTANCE) {
            try {
                actor.createActor();
                actor.getActor().preStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (envelope.getMessage() == PoisonPill.INSTANCE) {
            try {
                actor.getActor().postStop();
                actor.shutdownActor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            actor.getActor().onReceive(envelope.getMessage());
        }
    }
}
