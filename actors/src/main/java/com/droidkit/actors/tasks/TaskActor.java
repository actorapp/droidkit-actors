package com.droidkit.actors.tasks;

import com.droidkit.actors.Actor;
import com.droidkit.actors.messages.PoisonPill;

import java.util.HashSet;

/**
 * Created by ex3ndr on 17.08.14.
 */
public abstract class TaskActor<T> extends Actor {
    private final HashSet<TaskRequest> requests = new HashSet<TaskRequest>();

    private T result;
    private boolean isCompleted;
    private long dieTimeout = 300;

    public void setTimeOut(long timeOut) {
        dieTimeout = timeOut;
    }

    @Override
    public void preStart() {
        startTask();
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof TaskRequest) {
            TaskRequest request = (TaskRequest) message;
            if (isCompleted) {
                reply(result);
            } else {
                requests.add(request);
            }
        } else if (message instanceof Result) {
            if (!isCompleted) {
                Result res = (Result) message;
                isCompleted = true;
                result = (T) res.getRes();
                for (TaskRequest request : requests) {
                    request.getRef().send(new TaskResult<T>(request.getRequestId(), result));
                }
                self().send(PoisonPill.INSTANCE, dieTimeout);
            }
        } else if (message instanceof Error) {
            if (!isCompleted) {
                Error error = (Error) message;
                for (TaskRequest request : requests) {
                    request.getRef().send(new TaskError(request.getRequestId(), error.getError()));
                }
                context().stopSelf();
            }
        }
    }

    public abstract void startTask();

    public void complete(T res) {
        self().send(new Result(res));
    }

    private static class Error {
        private Throwable error;

        private Error(Throwable error) {
            this.error = error;
        }

        public Throwable getError() {
            return error;
        }
    }

    private static class Result {
        private Object res;

        private Result(Object res) {
            this.res = res;
        }

        public Object getRes() {
            return res;
        }
    }
}