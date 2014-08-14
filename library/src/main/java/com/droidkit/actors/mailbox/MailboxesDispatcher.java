package com.droidkit.actors.mailbox;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorScope;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.dispatch.AbstractDispatchQueue;

import java.util.HashMap;

/**
 * Created by ex3ndr on 14.08.14.
 */
public class MailboxesDispatcher extends AbsMailboxesDispatcher {

    private final HashMap<Mailbox, ActorScope> mailboxes = new HashMap<Mailbox, ActorScope>();

    private final ActorSystem actorSystem;

    public MailboxesDispatcher(ActorSystem actorSystem, int count) {
        this(actorSystem, count, new MailboxesQueue());
    }

    public MailboxesDispatcher(ActorSystem actorSystem, int count, int priority) {
        this(actorSystem, priority, count, new MailboxesQueue());
    }

    public MailboxesDispatcher(ActorSystem actorSystem, int count, int priority, MailboxesQueue queue) {
        super(count, priority, queue);
        this.actorSystem = actorSystem;
    }

    public MailboxesDispatcher(ActorSystem actorSystem, int count, MailboxesQueue queue) {
        super(count, queue);
        this.actorSystem = actorSystem;
    }

    @Override
    public void connectScope(ActorScope actor) {
        Mailbox mailbox = new Mailbox(getQueue());
        actor.init(mailbox, new ActorRef(actorSystem, mailbox));
        synchronized (mailboxes) {
            mailboxes.put(mailbox, actor);
        }

    }

    public ActorScope getMailboxActor(Mailbox mailbox) {
        synchronized (mailboxes) {
            return mailboxes.get(mailbox);
        }
    }

    @Override
    protected void dispatchAction(Envelope envelope) {
        try {
            getQueue().lockMailbox(envelope.getMailbox());
            ActorScope actor = getMailboxActor(envelope.getMailbox());
            processEnvelope(envelope, actor);
        } finally {
            getQueue().unlockMailbox(envelope.getMailbox());
        }
    }
}