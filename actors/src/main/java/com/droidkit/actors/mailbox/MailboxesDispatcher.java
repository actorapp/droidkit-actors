package com.droidkit.actors.mailbox;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorScope;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.dispatch.AbstractDispatchQueue;

import java.util.HashMap;

/**
 * Main actor model dispatcher for multiple mailboxes
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class MailboxesDispatcher extends AbsMailboxesDispatcher {

    private final HashMap<Mailbox, ActorScope> mailboxes = new HashMap<Mailbox, ActorScope>();

    private final ActorSystem actorSystem;

    /**
     * Creating dispatcher for actor system
     *
     * @param actorSystem  system
     * @param threadsCount number of threads
     */
    public MailboxesDispatcher(ActorSystem actorSystem, int threadsCount) {
        this(actorSystem, threadsCount, new MailboxesQueue());
    }

    /**
     * Creating dispatcher for actor system
     *
     * @param actorSystem  system
     * @param threadsCount number of threads
     * @param priority     threads priority
     */
    public MailboxesDispatcher(ActorSystem actorSystem, int threadsCount, int priority) {
        this(actorSystem, priority, threadsCount, new MailboxesQueue());
    }

    /**
     * Creating dispatcher for actor system
     *
     * @param actorSystem  system
     * @param threadsCount number of threads
     * @param priority     threads priority
     * @param queue        Mailboxes queue
     */
    public MailboxesDispatcher(ActorSystem actorSystem, int threadsCount, int priority, MailboxesQueue queue) {
        super(threadsCount, priority, queue);
        this.actorSystem = actorSystem;
    }

    /**
     * Creating dispatcher for actor system
     *
     * @param actorSystem  system
     * @param threadsCount number of threads
     * @param queue        Mailboxes queue
     */
    public MailboxesDispatcher(ActorSystem actorSystem, int threadsCount, MailboxesQueue queue) {
        super(threadsCount, queue);
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

    /**
     * Getting mailbox for actor
     *
     * @param mailbox mailbox
     * @return ActorScope or null if there are no actors for mailbox
     */
    protected ActorScope getMailboxActor(Mailbox mailbox) {
        synchronized (mailboxes) {
            return mailboxes.get(mailbox);
        }
    }

    @Override
    protected void dispatchMessage(Envelope envelope) {
        try {
            ActorScope actor = getMailboxActor(envelope.getMailbox());
            processEnvelope(envelope, actor);
        } finally {
            // TODO: better design
            getQueue().unlockMailbox(envelope.getMailbox());
        }
    }
}