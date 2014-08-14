package com.droidkit.actors;

/**
 * Created by ex3ndr on 14.08.14.
 */
public final class Props<T extends Actor> {
    private static final int TYPE_DEFAULT = 1;
    private static final int TYPE_CREATOR = 2;

    private final Class<T> aClass;
    private final Object[] args;
    private final int type;
    private final ActorCreator<T> creator;

    private String dispatcher;

    private Props(Class<T> aClass, Object[] args, int type, ActorCreator<T> creator) {
        this.aClass = aClass;
        this.args = args;
        this.type = type;
        this.creator = creator;
    }

    public T create() throws Exception {
        if (type == TYPE_DEFAULT) {
            if (args == null || args.length == 0) {
                return aClass.newInstance();
            }
        } else if (type == TYPE_CREATOR) {
            return creator.create();
        }

        throw new RuntimeException("Unsupported create method");
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public Props<T> changeDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
        return this;
    }

    public static <T extends Actor> Props<T> create(Class<T> tClass) {
        return new Props(tClass, null, TYPE_DEFAULT, null);
    }

    public static <T extends Actor> Props<T> create(Class<T> clazz, ActorCreator<T> creator) {
        return new Props<T>(clazz, null, TYPE_CREATOR, creator);
    }
}
