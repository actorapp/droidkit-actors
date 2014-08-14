package com.droidkit.actors;

import com.droidkit.actors.mailbox.AbsMailboxesDispatcher;
import com.droidkit.actors.mailbox.Envelope;
import com.droidkit.actors.mailbox.MailboxesDispatcher;
import com.droidkit.actors.messages.StartActor;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by ex3ndr on 12.08.14.
 */
public class ActorSystem {

    private static final ActorSystem mainSystem = new ActorSystem();

    public static ActorSystem system() {
        return mainSystem;
    }

    private static final String DEFAULT_DISPATCHER = "default";

    private final HashMap<String, AbsMailboxesDispatcher> dispatchers = new HashMap<String, AbsMailboxesDispatcher>();
    private final HashMap<String, ActorScope> actors = new HashMap<String, ActorScope>();

    public ActorSystem() {
        addDispatcher(DEFAULT_DISPATCHER);
    }

    public void addDispatcher(String dispatcherId) {
        addDispatcher(dispatcherId, new MailboxesDispatcher(this, Runtime.getRuntime().availableProcessors()));
    }

    public void addDispatcher(String dispatcherId, AbsMailboxesDispatcher dispatcher) {
        synchronized (dispatchers) {
            if (dispatchers.containsKey(dispatcherId)) {
                return;
            }
            dispatchers.put(dispatcherId, dispatcher);
        }
    }

    public <T extends Actor> ActorRef actorOf(Class<T> actor, String path) {
        return actorOf(Props.create(actor), path);
    }

    public ActorRef actorOf(Props props, String path) {
        synchronized (actors) {
            ActorScope scope = actors.get(path);

            if (scope != null) {
                return scope.getActorRef();
            }

            AbsMailboxesDispatcher mailboxesDispatcher;
            synchronized (dispatchers) {
                mailboxesDispatcher = dispatchers.get(
                        props.getDispatcher() == null ? DEFAULT_DISPATCHER : props.getDispatcher());
            }

            scope = new ActorScope(UUID.randomUUID(), path, props);

            mailboxesDispatcher.connectScope(scope);
            mailboxesDispatcher.getQueue().putToQueue(new Envelope(StartActor.INSTANCE, scope.getMailbox(), null), ActorTime.currentTime());

            actors.put(path, scope);

            return scope.getActorRef();
        }
    }
}