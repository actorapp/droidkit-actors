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

    public MailboxesDispatcher(ActorSystem actorSystem) {
        this(actorSystem, new MailboxesQueue());

    }

    public MailboxesDispatcher(ActorSystem actorSystem, int priority) {
        this(actorSystem, priority, new MailboxesQueue());
    }

    public MailboxesDispatcher(ActorSystem actorSystem, int priority, MailboxesQueue queue) {
        super(priority, queue);
        this.actorSystem = actorSystem;
    }

    public MailboxesDispatcher(ActorSystem actorSystem, MailboxesQueue queue) {
        super(queue);
        this.actorSystem = actorSystem;
    }

    @Override
    public void connectScope(ActorScope actor) {
        Mailbox mailbox = new Mailbox((MailboxesQueue) getQueue());
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
        ActorScope actor = getMailboxActor(envelope.getMailbox());
        processEnvelope(envelope, actor);
    }
}