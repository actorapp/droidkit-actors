package com.droidkit.actors.mailbox;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorScope;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.dispatch.AbstractDispatchQueue;
import com.droidkit.actors.messages.DeadLetter;
import com.droidkit.actors.messages.StartActor;

import java.util.HashMap;
import java.util.UUID;

/**
 * Main actor model dispatcher for multiple mailboxes
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class MailboxesDispatcher extends AbsMailboxesDispatcher {

    private final HashMap<Mailbox, ActorScope> mailboxes = new HashMap<Mailbox, ActorScope>();
    private final HashMap<String, ActorScope> scopes = new HashMap<String, ActorScope>();
    private final HashMap<String, Props> actorProps = new HashMap<String, Props>();

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
    public ActorScope createScope(String path, Props props) {
        // TODO: add path check

        Mailbox mailbox = new Mailbox(getQueue());
        UUID uuid = UUID.randomUUID();
        ActorRef ref = new ActorRef(actorSystem, this, uuid, path);
        ActorScope scope = new ActorScope(actorSystem, mailbox, ref, this, UUID.randomUUID(), path, props);

        synchronized (mailboxes) {
            mailboxes.put(mailbox, scope);
            scopes.put(scope.getPath(), scope);
            actorProps.put(path, props);
        }

        // Sending init message
        scope.getActorRef().send(StartActor.INSTANCE);
        return scope;
    }

    @Override
    public void disconnectScope(ActorScope scope) {
        synchronized (mailboxes) {
            mailboxes.remove(scope.getMailbox());
            scopes.remove(scope.getPath());
        }
        for (Envelope envelope : scope.getMailbox().allEnvelopes()) {
            if (envelope.getSender() != null) {
                envelope.getSender().send(new DeadLetter(envelope.getMessage()));
            }
        }
    }

    @Override
    public void sendMessage(String path, Object message, long time, ActorRef sender) {
        synchronized (mailboxes) {
            if (!scopes.containsKey(path)) {
                if (sender != null) {
                    sender.send(new DeadLetter(message));
                }
            } else {
                Mailbox mailbox = scopes.get(path).getMailbox();
                mailbox.schedule(new Envelope(message, mailbox, sender), time);
            }
        }
    }

    @Override
    public void sendMessageOnce(String path, Object message, long time, ActorRef sender) {
        synchronized (mailboxes) {
            if (!scopes.containsKey(path)) {
                if (sender != null) {
                    sender.send(new DeadLetter(message));
                }
            } else {
                Mailbox mailbox = scopes.get(path).getMailbox();
                mailbox.scheduleOnce(new Envelope(message, mailbox, sender), time);
            }
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