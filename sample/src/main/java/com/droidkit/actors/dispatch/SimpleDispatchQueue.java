package com.droidkit.actors.dispatch;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class SimpleDispatchQueue<T> extends AbstractDispatchQueue<T> {

    protected final TreeMap<Long, Message> messages = new TreeMap<Long, Message>();

    protected final ArrayList<Message> freeMessages = new ArrayList<Message>();

    @Override
    public T dispatch(long time) {
        synchronized (messages) {
            if (messages.size() > 0) {
                long firstKey = messages.firstKey();
                if (firstKey < time) {
                    Message message = messages.remove(firstKey);
                    T res = message.action;
                    recycle(message);
                    return res;
                }
            }
        }
        return null;
    }

    @Override
    public long waitDelay(long time) {
        synchronized (messages) {
            if (messages.size() > 0) {
                long firstKey = messages.firstKey();
                if (firstKey < time) {
                    return 0;
                } else {
                    return time - firstKey;
                }
            }
        }
        return FOREVER;
    }

    @Override
    public void putToQueueImpl(T action, long atTime) {
        Message message = obtainMessage();
        message.setMessage(action, atTime);
        synchronized (messages) {
            while (messages.containsKey(atTime)) {
                atTime++;
            }
            messages.put(atTime, message);
        }
    }

    protected Message obtainMessage() {
        synchronized (freeMessages) {
            if (freeMessages.size() > 0) {
                return freeMessages.remove(0);
            }
        }
        return new Message();
    }

    protected void recycle(Message message) {
        synchronized (freeMessages) {
            freeMessages.add(message);
        }
    }

    protected class Message {
        public long destTime;
        public T action;

        public void setMessage(T action, long destTime) {
            this.action = action;
            this.destTime = destTime;
        }
    }
}