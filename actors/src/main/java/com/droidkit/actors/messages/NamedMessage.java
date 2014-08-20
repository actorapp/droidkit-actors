package com.droidkit.actors.messages;

/**
 * Created by ex3ndr on 20.08.14.
 */
public class NamedMessage {
    private String name;
    private Object message;

    public NamedMessage(String name, Object message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public Object getMessage() {
        return message;
    }
}
