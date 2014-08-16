package com.droidkit.actors.sample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.Props;
import com.droidkit.actors.mailbox.MailboxesDispatcher;

import static com.droidkit.actors.ActorSystem.system;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        system().addDispatcher("images", new MailboxesDispatcher(system(), 2, Thread.MIN_PRIORITY));

        ActorRef log1 = system().actorOf(LogActor.class, "log");
        ActorRef log2 = system().actorOf(LogActor.class, "log");
        ActorRef log3 = system().actorOf(Props.create(LogActor.class).changeDispatcher("images"), "log2");
        ActorRef log4 = system().actorOf(Props.create(LogActor.class).changeDispatcher("images"), "log3");

        ActorRef[] refs = new ActorRef[]{log1, log2, log3, log4};
        for (int i = 0; i < 100; i++) {
            refs[i % refs.length].send("test" + i);
        }
        // log4.send("test4");
    }
}