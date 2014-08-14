package com.droidkit.actors.dispatch;

import static com.droidkit.actors.ActorTime.currentTime;

/**
 * MessageDispatcher is used for dispatching messages on it's own thread.
 * Automatically starts new thread for dispatching.
 * Class is completely thread-safe and it could collect actions before real thread start.
 * <p/>
 * Author: Stepan Ex3NDR Korshakov (me@ex3ndr.com, telegram: +7-931-342-12-48)
 */
public abstract class AbstractDispatcher<T> {

    private final Thread thread;
    final private AbstractDispatchQueue<T> queue;

    private boolean isClosed = false;

    public AbstractDispatcher() {
        this(Thread.NORM_PRIORITY);
    }

    public AbstractDispatcher(int priority) {
        this(priority, new SimpleDispatchQueue<T>());
    }

    public AbstractDispatcher(AbstractDispatchQueue<T> queue) {
        this(Thread.NORM_PRIORITY, queue);
    }

    public AbstractDispatcher(int priority, final AbstractDispatchQueue<T> queue) {
        this.queue = queue;

        this.thread = new DispatcherThread();
        this.thread.setPriority(priority);
        this.thread.start();

        this.queue.setListener(new QueueListener() {
            @Override
            public void onQueueChanged() {
                notifyDispatcher();
            }
        });
    }

    public AbstractDispatchQueue<T> getQueue() {
        return queue;
    }

    public void close() {
        isClosed = true;
        notifyDispatcher();
    }

    protected abstract void dispatchAction(T object);

    protected void notifyDispatcher() {
        synchronized (thread) {
            thread.notifyAll();
        }
    }

    private class DispatcherThread extends Thread {
        @Override
        public void run() {
            while (!isClosed) {
                T action = queue.dispatch(currentTime());
                if (action == null) {
                    synchronized (this) {
                        try {
                            long delay = queue.waitDelay(currentTime());
                            if (delay > 0) {
                                wait(delay);
                            }
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }

                try {
                    dispatchAction(action);
                } catch (Throwable t) {
                    // Possibly danger situation, but i hope this will not corrupt JVM
                    // For example: on Android we could always continue execution after OutOfMemoryError
                    // Anyway, better to catch all errors manually in dispatchAction
                    // t.printStackTrace();
                }
            }
        }
    }
}