package com.droidkit.actors.mailbox;

import com.droidkit.actors.*;
import com.droidkit.actors.dispatch.AbstractDispatcher;
import com.droidkit.actors.extensions.ActorExtension;
import com.droidkit.actors.messages.DeadLetter;
import com.droidkit.actors.messages.Ping;
import com.droidkit.actors.messages.PoisonPill;
import com.droidkit.actors.messages.StartActor;

import java.util.HashMap;
import java.util.UUID;

/**
 * Abstract Actor Dispatcher, used for dispatching messages for actors
 */
public abstract class AbsActorDispatcher {

    private final Object LOCK = new Object();
    private final HashMap<String, ActorEndpoint> endpoints = new HashMap<String, ActorEndpoint>();
    private final HashMap<String, ActorScope> scopes = new HashMap<String, ActorScope>();

    private final ActorSystem actorSystem;
    private String name;
    private AbstractDispatcher<Envelope, MailboxesQueue> dispatcher;

    public AbsActorDispatcher(String name, ActorSystem actorSystem) {
        this.name = name;
        this.actorSystem = actorSystem;
    }

    protected void initDispatcher(AbstractDispatcher<Envelope, MailboxesQueue> dispatcher) {
        if (this.dispatcher != null) {
            throw new RuntimeException("Double dispatcher init");
        }
        this.dispatcher = dispatcher;
    }

    public final ActorRef referenceActor(String path, Props props) {
        synchronized (LOCK) {
            if (scopes.containsKey(path)) {
                return scopes.get(path).getActorRef();
            }

            Mailbox mailbox = props.createMailbox(dispatcher.getQueue());

            ActorEndpoint endpoint = endpoints.get(path);
            if (endpoint == null) {
                endpoint = new ActorEndpoint(path);
                endpoints.put(path, endpoint);
            }

            ActorScope scope = new ActorScope(actorSystem, mailbox, this, UUID.randomUUID(), path, props, endpoint);
            endpoint.connect(mailbox, scope);
            scopes.put(scope.getPath(), scope);

            // Sending init message
            scope.getActorRef().send(StartActor.INSTANCE);
            return scope.getActorRef();
        }
    }

    public final void killGracefully(ActorScope scope) {
        for (ActorExtension e : scope.getActor().getExtensions()) {
            e.postStop();
        }
        scope.getActor().postStop();
        scope.onActorDie();

        for (Envelope e : scope.getMailbox().allEnvelopes()) {
            if (e.getSender() != null) {
                e.getSender().send(new DeadLetter(e.getMessage()));
            }
        }
        scope.getMailbox().clear();
    }

    public final void sendMessage(ActorEndpoint endpoint, Object message, long time, ActorRef sender) {
        if (endpoint.isDisconnected()) {
            if (sender != null) {
                if (actorSystem.getTraceInterface() != null) {
                    actorSystem.getTraceInterface().onDeadLetter(sender, message);
                }
                sender.send(new DeadLetter(message));
            }
        } else {
            endpoint.getMailbox().schedule(new Envelope(message, endpoint.getScope(), endpoint.getMailbox(), sender), time);
        }
    }

    public final void sendMessageOnce(ActorEndpoint endpoint, Object message, long time, ActorRef sender) {
        if (endpoint.isDisconnected()) {
            if (sender != null) {
                if (actorSystem.getTraceInterface() != null) {
                    actorSystem.getTraceInterface().onDeadLetter(sender, message);
                }
                sender.send(new DeadLetter(message));
            }
        } else {
            endpoint.getMailbox().scheduleOnce(new Envelope(message, endpoint.getScope(), endpoint.getMailbox(), sender), time);
        }
    }


    public String getName() {
        return name;
    }

    /**
     * Processing of envelope
     *
     * @param envelope envelope
     */
    protected void processEnvelope(Envelope envelope) {
        ActorScope scope = envelope.getScope();

        if (actorSystem.getTraceInterface() != null) {
            actorSystem.getTraceInterface().onEnvelopeDelivered(envelope);
        }

        long start = ActorTime.currentTime();

        if (scope.getActor() == null) {
            if (envelope.getMessage() == PoisonPill.INSTANCE) {
                // Not creating actor for PoisonPill
                return;
            }
            try {
                Actor actor = scope.getProps().create();
                CurrentActor.setCurrentActor(actor);
                actor.initActor(scope.getUuid(), scope.getPath(), new ActorContext(scope), scope.getMailbox());
                for (ActorExtension e : actor.getExtensions()) {
                    e.preStart();
                }
                actor.preStart();
                scope.onActorCreated(actor);
            } catch (Exception e) {
                e.printStackTrace();
                if (envelope.getSender() != null) {
                    envelope.getSender().send(new DeadLetter("Unable to create actor"));
                }
                return;
            }
        }

        try {
            if (envelope.getMessage() == StartActor.INSTANCE) {
                // Already created actor
                return;
            } else if (envelope.getMessage() == Ping.INSTANCE) {
                // No op
                return;
            } else if (envelope.getMessage() == PoisonPill.INSTANCE) {
                for (ActorExtension e : scope.getActor().getExtensions()) {
                    e.postStop();
                }
                scope.getActor().postStop();
                scope.onActorDie();
                for (Envelope e : scope.getMailbox().allEnvelopes()) {
                    if (e.getSender() != null) {
                        e.getSender().send(new DeadLetter(e.getMessage()));
                    }
                }
                scope.getMailbox().clear();
            } else {
                CurrentActor.setCurrentActor(scope.getActor());
                scope.setSender(envelope.getSender());
                for (ActorExtension e : scope.getActor().getExtensions()) {
                    if (e.onReceive(envelope.getMessage())) {
                        return;
                    }
                }
                scope.getActor().onReceive(envelope.getMessage());
            }
        } catch (Exception e) {
            if (actorSystem.getTraceInterface() != null) {
                actorSystem.getTraceInterface().onActorDie(scope.getActorRef(), e);
            }
            scope.onActorDie();
        } finally {
            if (actorSystem.getTraceInterface() != null) {
                actorSystem.getTraceInterface().onEnvelopeProcessed(envelope, ActorTime.currentTime() - start);
            }
            CurrentActor.setCurrentActor(null);
            dispatcher.getQueue().unlockMailbox(envelope.getMailbox());
        }
    }
}
