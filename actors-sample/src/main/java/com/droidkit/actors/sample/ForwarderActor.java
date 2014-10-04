package com.droidkit.actors.sample;

import com.droidkit.actors.*;

/**
 * Created by ex3ndr on 05.09.14.
 */
public class ForwarderActor extends Actor {

    public static ActorSelection selection(final ActorRef ref, int index) {
        return new ActorSelection(Props.create(ForwarderActor.class, new ActorCreator<ForwarderActor>() {
            @Override
            public ForwarderActor create() {
                return new ForwarderActor(ref);
            }
        }), "frw_" + index);
    }

    private ActorRef forwarder;
    int last = -1;

    public ForwarderActor(ActorRef forwarder) {
        this.forwarder = forwarder;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Integer) {
            int val = (Integer) message;
            if (last != val - 1) {
                Log.d(getPath() + "|Error! Wrong order expected #" + (last + 1) + " got #" + val);
            }
            last++;
            forwarder.send(message, self());
        }
    }
}
