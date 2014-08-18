package com.droidkit.actors;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class CurrentActor {
    private static ThreadLocal<Actor> currentActor = new ThreadLocal<Actor>();

    public static void setCurrentActor(Actor actor) {
        currentActor.set(actor);
    }

    public static Actor getCurrentActor() {
        return currentActor.get();
    }
}
