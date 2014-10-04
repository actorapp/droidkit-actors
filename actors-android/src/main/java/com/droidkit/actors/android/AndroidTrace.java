package com.droidkit.actors.android;

import android.util.Log;
import com.droidkit.actors.Actor;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.debug.TraceInterface;
import com.droidkit.actors.mailbox.Envelope;
import com.droidkit.actors.typed.messages.TypedRequest;

/**
 * Created by ex3ndr on 04.10.14.
 */
public class AndroidTrace implements TraceInterface {

    private static final long ENVELOPE_WARRING = 100;
    private static final String DEFAULT_TAG = "ActorTrace";

    public static void initTrace(ActorSystem system) {
        system.setTraceInterface(new AndroidTrace());
    }

    public static void initTrace(ActorSystem system, String tag) {
        system.setTraceInterface(new AndroidTrace(tag));
    }

    private String tag;

    public AndroidTrace(String tag) {
        this.tag = tag;
    }

    public AndroidTrace() {
        this(DEFAULT_TAG);
    }

    @Override
    public void onEnvelopeDelivered(Envelope envelope) {

    }

    @Override
    public void onEnvelopeProcessed(Envelope envelope, long duration) {
        if (envelope.getMessage() == null || envelope.getScope() == null || envelope.getScope().getActor() == null) {
            return;
        }

        String name = envelope.getMessage().getClass().getSimpleName();
        if (envelope.getMessage() instanceof TypedRequest) {
            name = ((TypedRequest) envelope.getMessage()).getMethod().getName();
        }

        String dispatcher = envelope.getScope().getDispatcher().getName();

        String actor = envelope.getScope().getActor().getClass().getSimpleName();

        if (duration > ENVELOPE_WARRING) {
            Log.w(tag, "Too long dispatch: " + dispatcher + "|" + actor + "~~" + name + " in " + duration + " ms");
        }
    }

    @Override
    public void onDrop(ActorRef sender, Object message, Actor actor) {
        String senderName = sender == null ? "<unknown>" : sender.getPath();
        Object messageName = message == null ? "<null>" : message + "";
        Log.w(tag, actor.self().getPath() + "|Dropped:" + messageName + " for " + senderName);
    }

    @Override
    public void onDeadLetter(ActorRef receiver, Object message) {
        Log.w(tag, receiver.getPath() + "|DeadLetter:" + message);
    }

    @Override
    public void onActorDie(ActorRef ref, Exception e) {
        e.printStackTrace();
        Log.w(tag, "Actor die " + ref.getPath() + ", ex: " + e.getClass().getSimpleName() + ":" + e.getMessage());
    }
}
