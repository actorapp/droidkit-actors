package com.droidkit.actors.dispatch;

import static com.droidkit.actors.ActorTime.currentTime;

/**
 * MessageDispatcher is used for dispatching messages on it's own thread.
 * Automatically starts new thread for dispatching.
 * Class is completely thread-safe and it could collect actions before real thread start.
 * <p/>
 * Author: Stepan Ex3NDR Korshakov (me@ex3ndr.com, telegram: +7-931-342-12-48)
 */
public abstract class AbstractDispatcher<T, Q extends AbstractDispatchQueue<T>> {

    private final Thread[] threads;
    final private Q queue;

    private boolean isClosed = false;

    public AbstractDispatcher(int count, Q queue, int priority) {
        this(count, priority, queue);
    }

    public AbstractDispatcher(int count, Q queue) {
        this(count, Thread.NORM_PRIORITY, queue);
    }

    public AbstractDispatcher(int count, int priority, final Q queue) {
        this.queue = queue;

        this.threads = new Thread[count];
        for (int i = 0; i < count; i++) {
            this.threads[i] = new DispatcherThread();
            this.threads[i].setPriority(priority);
            this.threads[i].start();
        }

        this.queue.setListener(new QueueListener() {
            @Override
            public void onQueueChanged() {
                notifyDispatcher();
            }
        });
    }

    public Q getQueue() {
        return queue;
    }

    public void close() {
        isClosed = true;
        notifyDispatcher();
    }

    protected abstract void dispatchAction(T object);

    protected void notifyDispatcher() {
        synchronized (threads) {
            threads.notifyAll();
        }
    }

    private class DispatcherThread extends Thread {
        @Override
        public void run() {
            while (!isClosed) {
                T action = queue.dispatch(currentTime());
                if (action == null) {
                    synchronized (threads) {
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