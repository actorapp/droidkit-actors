package com.droidkit.actors.sample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.droidkit.actors.ActorRef;

import static com.droidkit.actors.ActorSystem.system;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActorRef log1 = system().actorOf(LogActor.class, "log");
        ActorRef log2 = system().actorOf(LogActor.class, "log");

        log1.send("test1");
        log1.send("test2");
        log2.send("test3");
        log2.send("test4");
    }
}