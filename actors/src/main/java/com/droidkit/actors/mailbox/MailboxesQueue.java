package com.droidkit.actors.mailbox;

import com.droidkit.actors.dispatch.AbstractDispatchQueue;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by ex3ndr on 13.08.14.
 */
public class MailboxesQueue extends AbstractDispatchQueue<Envelope> {

    private final TreeMap<Long, Envelope> envelopes = new TreeMap<Long, Envelope>();
    private final HashSet<Mailbox> blocked = new HashSet<Mailbox>();

    public synchronized void lockMailbox(Mailbox mailbox) {
        blocked.add(mailbox);
    }

    public synchronized void unlockMailbox(Mailbox mailbox) {
        blocked.remove(mailbox);
        notifyQueueChanged();
    }

    public synchronized long sendEnvelope(Envelope envelope, long time) {
        while (envelopes.containsKey(time)) {
            time++;
        }
        envelopes.put(time, envelope);
        notifyQueueChanged();
        return time;
    }

    public synchronized void removeEnvelope(long id) {
        envelopes.remove(id);
        notifyQueueChanged();
    }

    private synchronized Map.Entry<Long, Envelope> firstEnvelope() {
        for (Map.Entry<Long, Envelope> entry : envelopes.entrySet()) {
            if (blocked.contains(entry.getValue().getMailbox())) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public synchronized Envelope dispatch(long time) {
        Map.Entry<Long, Envelope> envelope = firstEnvelope();
        if (envelope != null) {
            if (envelope.getKey() < time) {
                envelopes.remove(envelope.getKey());
                return envelope.getValue();
            }
        }
        return null;
    }

    @Override
    public synchronized long waitDelay(long time) {
        Map.Entry<Long, Envelope> envelope = firstEnvelope();
        if (envelope != null) {
            if (envelope.getKey() <= time) {
                return 0;
            } else {
                return time - envelope.getKey();
            }
        }
        return FOREVER;
    }

    @Override
    protected void putToQueueImpl(Envelope message, long atTime) {
        sendEnvelope(message, atTime);
    }
}
