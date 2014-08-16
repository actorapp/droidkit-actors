package com.droidkit.actors;

import com.droidkit.actors.mailbox.Mailbox;

import java.util.UUID;

/**
 * Actor object
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class Actor {

    private UUID uuid;
    private String path;

    private ActorContext context;
    private Mailbox mailbox;

    public Actor() {

    }

    /**
     * INTERNAL API
     * <p/>
     * Initialization of actor
     *
     * @param uuid    uuid of actor
     * @param path    path of actor
     * @param context context of actor
     * @param mailbox mailbox of actor
     */
    public final void initActor(UUID uuid, String path, ActorContext context, Mailbox mailbox) {
        this.uuid = uuid;
        this.path = path;
        this.context = context;
        this.mailbox = mailbox;
    }

    /**
     * Actor System
     *
     * @return Actor System
     */
    protected final ActorSystem system() {
        return context.getSystem();
    }

    /**
     * Self actor reference
     *
     * @return self reference
     */
    protected final ActorRef self() {
        return context.getSelf();
    }

    /**
     * Actor context
     *
     * @return context
     */
    protected final ActorContext context() {
        return context;
    }

    /**
     * Actor UUID
     *
     * @return uuid
     */
    protected final UUID getUuid() {
        return uuid;
    }

    /**
     * Actor path
     *
     * @return path
     */
    protected final String getPath() {
        return path;
    }

    /**
     * Actor mailbox
     *
     * @return mailbox
     */
    public final Mailbox getMailbox() {
        return mailbox;
    }

    /**
     * Called before first message receiving
     */
    public void preStart() {

    }

    /**
     * Receiving of message
     *
     * @param message message
     */
    public void onReceive(Object message) {

    }

    /**
     * Called after actor shutdown
     */
    public void postStop() {

    }
}
