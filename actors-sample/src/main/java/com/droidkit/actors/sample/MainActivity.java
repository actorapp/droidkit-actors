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
//        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg");
//        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg", 600);
//        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg", 3000);

        ActorRef dow2 = system().actorOf(DownloadFile.class, "dow2");
        dow2.send(new String[]{
                "http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg",
                "http://isc.stuorg.iastate.edu/wp-content/uploads/sample.jpg",
                "http://imgsv.imaging.nikon.com/lineup/lens/zoom/normalzoom/af-s_dx_18-300mmf_35-56g_ed_vr/img/sample/sample4_l.jpg"});
    }
}