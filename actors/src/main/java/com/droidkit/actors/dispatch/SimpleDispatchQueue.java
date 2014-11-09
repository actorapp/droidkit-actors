package com.droidkit.actors.dispatch;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Simple queue implementation for dispatchers
 *
 * @author Steve Ex3NDR Korshakov (steve@actor.im)
 */
public class SimpleDispatchQueue<T> extends AbstractDispatchQueue<T> {

    protected final TreeMap<Long, Message> messages = new TreeMap<Long, Message>();

    protected final ArrayList<Message> freeMessages = new ArrayList<Message>();

    /**
     * Removing message from queue
     *
     * @param t message
     */
    public void removeFromQueue(T t) {
        synchronized (messages) {
            for (Map.Entry<Long, Message> messageEntry : messages.entrySet()) {
                if (messageEntry.getValue().equals(t)) {
                    Message message = messages.remove(messageEntry.getKey());
                    recycle(message);
                    notifyQueueChanged();
                    return;
                }
            }
        }
    }

    @Override
    public DispatchResult dispatch(long time) {
        synchronized (messages) {
            if (messages.size() > 0) {
                long firstKey = messages.firstKey();
                if (firstKey < time) {
                    Message message = messages.remove(firstKey);
                    T res = message.action;
                    recycle(message);
                    return result(res);
                } else {
                    return delay(time - firstKey);
                }
            }
        }
        return delay(FOREVER);
    }

    public void putToQueue(T action, long atTime) {
        Message message = obtainMessage();
        message.setMessage(action, atTime);
        synchronized (messages) {
            while (messages.containsKey(atTime)) {
                atTime++;
            }
            messages.put(atTime, message);
        }
        notifyQueueChanged();
    }

    /**
     * Getting new message object for writing to queue
     *
     * @return Message object
     */
    protected Message obtainMessage() {
        synchronized (freeMessages) {
            if (freeMessages.size() > 0) {
                return freeMessages.remove(0);
            }
        }
        return new Message();
    }

    /**
     * Saving message object to free cache
     *
     * @param message Message object
     */
    protected void recycle(Message message) {
        synchronized (freeMessages) {
            freeMessages.add(message);
        }
    }

    /**
     * Holder for messages
     */
    protected class Message {
        public long destTime;
        public T action;

        public void setMessage(T action, long destTime) {
            this.action = action;
            this.destTime = destTime;
        }
    }
}