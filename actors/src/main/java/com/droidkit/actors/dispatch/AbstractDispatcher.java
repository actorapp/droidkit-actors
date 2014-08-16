package com.droidkit.actors.dispatch;

import static com.droidkit.actors.ActorTime.currentTime;

/**
 * MessageDispatcher is used for dispatching messages on it's own threads.
 * Class is completely thread-safe.
 *
 * @author Stepan Ex3NDR Korshakov (me@ex3ndr.com)
 */
public abstract class AbstractDispatcher<T, Q extends AbstractDispatchQueue<T>> {

    private final Thread[] threads;
    final private Q queue;

    private boolean isClosed = false;

    /**
     * Dispatcher constructor. Create threads with NORM_PRIORITY.
     *
     * @param count thread count
     * @param queue queue for messages
     *              (see {@link com.droidkit.actors.dispatch.AbstractDispatchQueue} for more information)
     */
    public AbstractDispatcher(int count, Q queue) {
        this(count, Thread.NORM_PRIORITY, queue);
    }

    /**
     * Dispatcher constructor
     *
     * @param count    thread count
     * @param queue    queue for messages
     *                 (see {@link com.droidkit.actors.dispatch.AbstractDispatchQueue} for more information)
     * @param priority thread priority
     */
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

    /**
     * Queue used for dispatching
     *
     * @return queue
     */
    public Q getQueue() {
        return queue;
    }

    /**
     * Closing of dispatcher no one actions will be executed after calling this method.
     */
    public void close() {
        isClosed = true;
        notifyDispatcher();
    }

    /**
     * Actual execution of action
     *
     * @param message action
     */
    protected abstract void dispatchMessage(T message);

    /**
     * Notification about queue change
     */
    protected void notifyDispatcher() {
        synchronized (threads) {
            threads.notifyAll();
        }
    }

    /**
     * Thread class for dispatching
     */
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
                    dispatchMessage(action);
                } catch (Throwable t) {
                    // Possibly danger situation, but i hope this will not corrupt JVM
                    // For example: on Android we could always continue execution after OutOfMemoryError
                    // Anyway, better to catch all errors manually in dispatchMessage
                    // t.printStackTrace();
                }
            }
        }
    }
}