package com.droidkit.actors;

import com.droidkit.actors.mailbox.Envelope;
import com.droidkit.actors.mailbox.Mailbox;

/**
 * Created by ex3ndr on 13.08.14.
 */
public class ActorRef {
    private ActorSystem system;
    private Mailbox mailbox;

    public ActorRef(ActorSystem system, Mailbox mailbox) {
        this.system = system;
        this.mailbox = mailbox;
    }

    public void send(Object message) {
        send(message, null);
    }

    public void send(Object message, ActorRef sender) {
        send(message, 0, sender);
    }

    public void send(Object message, long delay) {
        send(message, delay, null);
    }

    public void send(Object message, long delay, ActorRef sender) {
        mailbox.schedule(new Envelope(message, mailbox, sender), ActorTime.currentTime() + delay);
    }

    public void sendOnce(Object message) {
        send(message, null);
    }

    public void sendOnce(Object message, ActorRef sender) {
        sendOnce(message, 0, sender);
    }

    public void sendOnce(Object message, long delay) {
        sendOnce(message, delay, null);
    }

    public void sendOnce(Object message, long delay, ActorRef sender) {
        mailbox.scheduleOnce(new Envelope(message, mailbox, sender), ActorTime.currentTime() + delay);
    }
}
