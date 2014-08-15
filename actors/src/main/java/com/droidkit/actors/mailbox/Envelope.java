package com.droidkit.actors.mailbox;

import com.droidkit.actors.ActorRef;

/**
 * Created by ex3ndr on 13.08.14.
 */
public class Envelope {
    private final Object message;
    private final ActorRef sender;
    private final Mailbox mailbox;

    public Envelope(Object message, Mailbox mailbox, ActorRef sender) {
        this.message = message;
        this.sender = sender;
        this.mailbox = mailbox;
    }

    public Object getMessage() {
        return message;
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    public ActorRef getSender() {
        return sender;
    }
}
