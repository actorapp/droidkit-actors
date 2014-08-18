package com.droidkit.actors.messages;

/**
 * Created by ex3ndr on 17.08.14.
 */
public class DeadLetter {
    private Object message;

    public DeadLetter(Object message) {
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DeadLetter(" + message + ")";
    }
}
