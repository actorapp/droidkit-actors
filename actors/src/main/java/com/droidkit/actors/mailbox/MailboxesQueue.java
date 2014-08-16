package com.droidkit.actors.mailbox;

import com.droidkit.actors.dispatch.AbstractDispatchQueue;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Queue of multiple mailboxes for MailboxesDispatcher
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public class MailboxesQueue extends AbstractDispatchQueue<Envelope> {

    private final TreeMap<Long, Envelope> envelopes = new TreeMap<Long, Envelope>();
    private final HashSet<Mailbox> blocked = new HashSet<Mailbox>();

    /**
     * Locking mailbox from processing messages from it
     *
     * @param mailbox mailbox for locking
     */
    public void lockMailbox(Mailbox mailbox) {
        synchronized (blocked) {
            blocked.add(mailbox);
        }
        notifyQueueChanged();
    }

    /**
     * Unlocking mailbox
     *
     * @param mailbox mailbox for unlocking
     */
    public void unlockMailbox(Mailbox mailbox) {
        synchronized (blocked) {
            blocked.remove(mailbox);
        }
        notifyQueueChanged();
    }

    /**
     * Sending envelope
     *
     * @param envelope envelope
     * @param time     time (see {@link com.droidkit.actors.ActorTime#currentTime()}})
     * @return envelope real time
     */
    public long sendEnvelope(Envelope envelope, long time) {
        synchronized (envelopes) {
            while (envelopes.containsKey(time)) {
                time++;
            }
            envelopes.put(time, envelope);
        }
        notifyQueueChanged();
        return time;
    }

    /**
     * Removing envelope from queue
     *
     * @param id envelope id
     */
    public void removeEnvelope(long id) {
        synchronized (envelopes) {
            envelopes.remove(id);
        }
        notifyQueueChanged();
    }

    /**
     * getting first available envelope
     * MUST BE wrapped with envelopes and blocked sync
     *
     * @return envelope entry
     */
    private Map.Entry<Long, Envelope> firstEnvelope() {
        for (Map.Entry<Long, Envelope> entry : envelopes.entrySet()) {
            if (!blocked.contains(entry.getValue().getMailbox())) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public Envelope dispatch(long time) {
        synchronized (envelopes) {
            synchronized (blocked) {
                Map.Entry<Long, Envelope> envelope = firstEnvelope();
                if (envelope != null) {
                    if (envelope.getKey() < time) {
                        envelopes.remove(envelope.getKey());
                        //TODO: Better design
                        // Locking of mailbox before dispatch return
                        blocked.add(envelope.getValue().getMailbox());
                        return envelope.getValue();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public long waitDelay(long time) {
        synchronized (envelopes) {
            synchronized (blocked) {
                Map.Entry<Long, Envelope> envelope = firstEnvelope();

                if (envelope != null) {
                    if (envelope.getKey() <= time) {
                        return 0;
                    } else {
                        return time - envelope.getKey();
                    }
                }
            }
        }
        return FOREVER;
    }

    @Override
    protected void putToQueueImpl(Envelope message, long atTime) {
        sendEnvelope(message, atTime);
    }
}
