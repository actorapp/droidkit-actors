package com.droidkit.actors.tasks;

/**
 * Created by ex3ndr on 20.08.14.
 */
public interface AskCallback<T> {
    public void onResult(T result);

    public void onError(Throwable throwable);
}
