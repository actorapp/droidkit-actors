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

    public static void initTrace(ActorSystem system, Thread.UncaughtExceptionHandler handler) {
        system.setTraceInterface(new AndroidTrace(handler));
    }

    public static void initTrace(ActorSystem system, String tag) {
        system.setTraceInterface(new AndroidTrace(tag));
    }

    public static void initTrace(ActorSystem system, String tag, Thread.UncaughtExceptionHandler handler) {
        system.setTraceInterface(new AndroidTrace(tag, handler));
    }

    private String tag;

    private Thread.UncaughtExceptionHandler handler;

    public AndroidTrace(String tag, Thread.UncaughtExceptionHandler handler) {
        this.tag = tag;
        this.handler = handler;
    }

    public AndroidTrace(Thread.UncaughtExceptionHandler handler) {
        this(DEFAULT_TAG, handler);
    }

    public AndroidTrace() {
        this(DEFAULT_TAG, null);
    }

    public AndroidTrace(String tag) {
        this(tag, null);
    }

    @Override
    public void onEnvelopeDelivered(Envelope envelope) {

    }

    @Override
    public void onEnvelopeProcessed(Envelope envelope, long duration) {
        if (envelope.getMessage() == null || envelope.getScope() == null || envelope.getScope().getActor() == null) {
            return;
        }

        if (duration > ENVELOPE_WARRING) {
            String name = envelope.getMessage().getClass().getSimpleName();
            if (envelope.getMessage() instanceof TypedRequest) {
                name = ((TypedRequest) envelope.getMessage()).getMethod().getName();
            }

            String dispatcher = envelope.getScope().getDispatcher().getName();

            String actor = envelope.getScope().getActor().getClass().getSimpleName();

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
        if (handler != null) {
            handler.uncaughtException(Thread.currentThread(), e);
        }
        e.printStackTrace();
        Log.w(tag, "Actor die " + ref.getPath() + ", ex: " + e.getClass().getSimpleName() + ":" + e.getMessage());
    }
}