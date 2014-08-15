package com.droidkit.actors;

import com.droidkit.actors.mailbox.Mailbox;

import java.util.UUID;

/**
 * Created by ex3ndr on 12.08.14.
 */
public class Actor {

    private UUID uuid;
    private String path;

    private ActorContext context;
    private Mailbox mailbox;

    public Actor() {

    }

    public final void initActor(UUID uuid, String path, ActorContext context, Mailbox mailbox) {
        this.uuid = uuid;
        this.path = path;
        this.context = context;
        this.mailbox = mailbox;
    }

    protected final ActorSystem system() {
        return context.getSystem();
    }

    protected final ActorRef self() {
        return context.getSelf();
    }

    protected final ActorContext context() {
        return context;
    }

    protected final UUID getUuid() {
        return uuid;
    }

    protected final String getPath() {
        return path;
    }

    public final Mailbox getMailbox() {
        return mailbox;
    }

    public void preStart() {

    }

    public void onReceive(Object message) {

    }

    public void postStop() {

    }
}
