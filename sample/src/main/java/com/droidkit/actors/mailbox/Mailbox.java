package com.droidkit.actors.mailbox;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ex3ndr on 13.08.14.
 */
public class Mailbox {
    private final TreeMap<Long, Envelope> envelopes = new TreeMap<Long, Envelope>();

    private MailboxesQueue dispatcher;

    public Mailbox(MailboxesQueue dispatcher) {
        this.dispatcher = dispatcher;
    }

    public synchronized void schedule(Envelope envelope, long time) {
        if (envelope.getMailbox() != this) {
            throw new RuntimeException("envelope.mailbox != this mailbox");
        }

        time = dispatcher.sendEnvelope(envelope, time);
        envelopes.put(time, envelope);
    }

    public synchronized void scheduleOnce(Envelope envelope, long time) {
        if (envelope.getMailbox() != this) {
            throw new RuntimeException("envelope.mailbox != this mailbox");
        }

        Iterator<Map.Entry<Long, Envelope>> iterator = envelopes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Envelope> entry = iterator.next();
            if (isEqualEnvelope(entry.getValue(), envelope)) {
                dispatcher.removeEnvelope(entry.getKey());
                iterator.remove();
            }
        }

        schedule(envelope, time);
    }

    protected boolean isEqualEnvelope(Envelope a, Envelope b) {
        return a.getMessage().getClass() == b.getMessage().getClass();
    }
}