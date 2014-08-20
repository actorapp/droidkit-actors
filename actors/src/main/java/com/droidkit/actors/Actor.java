package com.droidkit.actors;

import com.droidkit.actors.mailbox.Mailbox;
import com.droidkit.actors.messages.DeadLetter;
import com.droidkit.actors.tasks.ActorAskImpl;
import com.droidkit.actors.tasks.AskCallback;
import com.droidkit.actors.tasks.TaskRequest;
import com.droidkit.actors.tasks.TaskResult;

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

    private ActorAskImpl askPattern;

    public Actor() {

    }

    /**
     * <p>INTERNAL API</p>
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
        this.askPattern = new ActorAskImpl(self());
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

    public final void onReceiveGlobal(Object message) {
        if (message instanceof DeadLetter) {
            if (askPattern.onDeadLetter((DeadLetter) message)) {
                return;
            }
        } else if (message instanceof TaskResult) {
            if (askPattern.onTaskResult((TaskResult) message)) {
                return;
            }
        }
        onReceive(message);
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

    /**
     * Reply message to sender of last message
     *
     * @param message reply message
     */
    public void reply(Object message) {
        if (context.sender() != null) {
            context.sender().send(message, self());
        }
    }

    public void ask(ActorSelection selection, AskCallback callback) {
        ask(system().actorOf(selection), callback);
    }

    public void ask(ActorRef ref, AskCallback callback) {
        askPattern.ask(ref, callback);
    }

}
