package com.droidkit.actors.sample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.Props;
import com.droidkit.actors.mailbox.MailboxesDispatcher;
import com.droidkit.actors.messages.PoisonPill;

import static com.droidkit.actors.ActorSystem.system;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActorRef log = system().actorOf(LogActor.class, "log");

        ActorRef downloader = system().actorOf(DownloadFile.class, "dow");
        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg");
        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg", 600);
        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg", 3000);
    }
}