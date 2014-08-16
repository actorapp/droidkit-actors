package com.droidkit.actors;

import com.droidkit.actors.mailbox.AbsMailboxesDispatcher;
import com.droidkit.actors.mailbox.Envelope;
import com.droidkit.actors.mailbox.MailboxesDispatcher;
import com.droidkit.actors.messages.StartActor;

import java.util.HashMap;
import java.util.UUID;

/**
 * Entry point for Actor Model, creates all actors and dispatchers
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class ActorSystem {

    private static final ActorSystem mainSystem = new ActorSystem();

    /**
     * Main actor system
     *
     * @return ActorSystem
     */
    public static ActorSystem system() {
        return mainSystem;
    }

    private static final String DEFAULT_DISPATCHER = "default";

    private final HashMap<String, AbsMailboxesDispatcher> dispatchers = new HashMap<String, AbsMailboxesDispatcher>();
    private final HashMap<String, ActorScope> actors = new HashMap<String, ActorScope>();

    /**
     * Creating new actor system
     */
    public ActorSystem() {
        addDispatcher(DEFAULT_DISPATCHER);
    }

    /**
     * Adding dispatcher with threads count = {@code Runtime.getRuntime().availableProcessors()}
     *
     * @param dispatcherId dispatcher id
     */
    public void addDispatcher(String dispatcherId) {
        addDispatcher(dispatcherId, new MailboxesDispatcher(this, Runtime.getRuntime().availableProcessors()));
    }

    /**
     * Registering custom dispatcher
     *
     * @param dispatcherId dispatcher id
     * @param dispatcher   dispatcher object
     */
    public void addDispatcher(String dispatcherId, AbsMailboxesDispatcher dispatcher) {
        synchronized (dispatchers) {
            if (dispatchers.containsKey(dispatcherId)) {
                return;
            }
            dispatchers.put(dispatcherId, dispatcher);
        }
    }

    /**
     * Creating or getting existing actor from actor class
     *
     * @param actor Actor Class
     * @param path  Actor Path
     * @param <T>   Actor Class
     * @return ActorRef
     */
    public <T extends Actor> ActorRef actorOf(Class<T> actor, String path) {
        return actorOf(Props.create(actor), path);
    }

    /**
     * Creating or getting existing actor from actor props
     *
     * @param props Actor Props
     * @param path  Actor Path
     * @return ActorRef
     */
    public ActorRef actorOf(Props props, String path) {
        // TODO: Remove lock
        synchronized (actors) {
            // Searching for already created actor
            ActorScope scope = actors.get(path);

            // If already created - return ActorRef
            if (scope != null) {
                return scope.getActorRef();
            }

            // Finding dispatcher for actor
            AbsMailboxesDispatcher mailboxesDispatcher;
            synchronized (dispatchers) {
                String dispatcherId = props.getDispatcher() == null ? DEFAULT_DISPATCHER : props.getDispatcher();
                if (!dispatchers.containsKey(dispatcherId)) {
                    throw new RuntimeException("Unknown dispatcherId '" + dispatcherId + "'");
                }
                mailboxesDispatcher = dispatchers.get(dispatcherId);
            }

            // Creating actor scope
            scope = new ActorScope(UUID.randomUUID(), path, props);

            // Connecting scope with dispatcher
            mailboxesDispatcher.connectScope(scope);
            // Sending initial message for creating actor
            mailboxesDispatcher.getQueue().putToQueue(new Envelope(StartActor.INSTANCE, scope.getMailbox(), null), ActorTime.currentTime());

            // Saving actor in collection
            actors.put(path, scope);

            return scope.getActorRef();
        }
    }
}