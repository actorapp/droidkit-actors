package com.droidkit.actors;

import com.droidkit.actors.mailbox.Envelope;
import com.droidkit.actors.mailbox.Mailbox;

/**
 * Reference to Actor that allows to send messages to real Actor
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class ActorRef {
    private ActorSystem system;
    private Mailbox mailbox;

    /**
     * INTERNAL API
     * <p/>
     * Creating actor reference
     *
     * @param system  actor system
     * @param mailbox mailbox
     */
    public ActorRef(ActorSystem system, Mailbox mailbox) {
        this.system = system;
        this.mailbox = mailbox;
    }

    /**
     * Send message with empty sender
     *
     * @param message message
     */
    public void send(Object message) {
        send(message, null);
    }

    /**
     * Send message with specified sender
     *
     * @param message message
     * @param sender  sender
     */
    public void send(Object message, ActorRef sender) {
        send(message, 0, sender);
    }

    /**
     * Send message with empty sender and delay
     *
     * @param message message
     * @param delay   delay
     */
    public void send(Object message, long delay) {
        send(message, delay, null);
    }

    /**
     * Send message
     *
     * @param message message
     * @param delay   delay
     * @param sender  sender
     */
    public void send(Object message, long delay, ActorRef sender) {
        mailbox.schedule(new Envelope(message, mailbox, sender), ActorTime.currentTime() + delay);
    }

    /**
     * Send message once
     *
     * @param message message
     */
    public void sendOnce(Object message) {
        send(message, null);
    }

    /**
     * Send message once
     *
     * @param message message
     * @param sender  sender
     */
    public void sendOnce(Object message, ActorRef sender) {
        sendOnce(message, 0, sender);
    }

    /**
     * Send message once
     *
     * @param message message
     * @param delay   delay
     */
    public void sendOnce(Object message, long delay) {
        sendOnce(message, delay, null);
    }

    /**
     * Send message once
     *
     * @param message message
     * @param delay   delay
     * @param sender  sender
     */
    public void sendOnce(Object message, long delay, ActorRef sender) {
        mailbox.scheduleOnce(new Envelope(message, mailbox, sender), ActorTime.currentTime() + delay);
    }
}
