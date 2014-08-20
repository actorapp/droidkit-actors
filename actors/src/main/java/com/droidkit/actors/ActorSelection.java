package com.droidkit.actors;

/**
 * Created by ex3ndr on 20.08.14.
 */
public class ActorSelection {
    private final Props props;
    private final String path;

    public ActorSelection(Props props, String path) {
        this.props = props;
        this.path = path;
    }

    public Props getProps() {
        return props;
    }

    public String getPath() {
        return path;
    }
}
