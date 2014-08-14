package com.droidkit.actors.mailbox;

import com.droidkit.actors.dispatch.AbstractDispatchQueue;

import java.util.TreeMap;

/**
 * Created by ex3ndr on 13.08.14.
 */
public class MailboxesQueue extends AbstractDispatchQueue<Envelope> {

    private final TreeMap<Long, Envelope> envelopes = new TreeMap<Long, Envelope>();

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

    @Override
    public synchronized Envelope dispatch(long time) {
        if (envelopes.size() > 0) {
            long firstKey = envelopes.firstKey();
            if (firstKey < time) {
                return envelopes.remove(firstKey);
            }
        }
        return null;
    }

    @Override
    public synchronized long waitDelay(long time) {
        if (envelopes.size() > 0) {
            long firstKey = envelopes.firstKey();
            if (firstKey < time) {
                return 0;
            } else {
                return time - firstKey;
            }
        }
        return FOREVER;
    }

    @Override
    protected void putToQueueImpl(Envelope message, long atTime) {
        sendEnvelope(message, atTime);
    }
}
