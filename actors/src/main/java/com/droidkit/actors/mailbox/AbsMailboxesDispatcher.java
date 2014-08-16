package com.droidkit.actors.mailbox;

import com.droidkit.actors.ActorScope;
import com.droidkit.actors.dispatch.AbstractDispatchQueue;
import com.droidkit.actors.dispatch.AbstractDispatcher;
import com.droidkit.actors.messages.PoisonPill;
import com.droidkit.actors.messages.StartActor;

/**
 * Abstract Dispatcher of mailboxes
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public abstract class AbsMailboxesDispatcher extends AbstractDispatcher<Envelope, MailboxesQueue> {

    protected AbsMailboxesDispatcher(int count, MailboxesQueue queue) {
        super(count, queue);
    }

    protected AbsMailboxesDispatcher(int count, int priority, MailboxesQueue queue) {
        super(count, priority, queue);
    }

    /**
     * Connecting ActorScope with Dispatcher
     *
     * @param actor scope
     */
    public abstract void connectScope(ActorScope actor);

    /**
     * Processing of envelope
     *
     * @param envelope envelope
     * @param actor    actor
     */
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
