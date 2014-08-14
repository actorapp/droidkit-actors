package com.droidkit.actors;

/**
 * Created by ex3ndr on 14.08.14.
 */
public interface ActorCreator<T extends Actor> {
    public T create();
}
