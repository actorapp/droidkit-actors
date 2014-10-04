package com.droidkit.actors.sample;

import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.*;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.droidkit.actors.*;
import com.droidkit.actors.android.UiActor;
import com.droidkit.actors.android.UiActorDispatcher;
import com.droidkit.actors.mailbox.Envelope;
import com.droidkit.actors.mailbox.Mailbox;
import com.droidkit.actors.mailbox.MailboxesQueue;

import static com.droidkit.actors.ActorSystem.system;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // system().addDispatcher("ui", new UiActorDispatcher(system()));

//        ActorRef log = system().actorOf(LogActor.class, "log");

//        ActorRef downloader = system().actorOf(DownloadFile.class, "dow");
//        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg");
//        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg", 600);
//        downloader.send("http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg", 3000);

//        ActorRef dow2 = system().actorOf(DownloadFile.class, "dow2");
//        dow2.send(new String[]{
//                "http://flirtyfleurs.com/wp-content/uploads/2012/10/pwg-sample-11_photo.jpg",
//                "http://isc.stuorg.iastate.edu/wp-content/uploads/sample.jpg",
//                "http://imgsv.imaging.nikon.com/lineup/lens/zoom/normalzoom/af-s_dx_18-300mmf_35-56g_ed_vr/img/sample/sample4_l.jpg"});

//        final TextView view = (TextView) findViewById(R.id.demo);
//        final UiActor actor = new UiActor() {
//            @Override
//            public void onReceive(Object message) {
//                view.setText(message.toString());
//            }
//        };

        findViewById(R.id.demoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (int i = 0; i < 10; i++) {
//                    actor.getActorRef().send("message_" + i, i * 500);
//                }

                new Thread() {
                    @Override
                    public void run() {
                        performBenchmark();
                    }
                }.start();


            }
        });

//        final ActorRef[] forwarder = new ActorRef[10];
//        forwarder[0] = system().actorOf(CounterActor.class, "counter");
//        for (int i = 1; i < 10; i++) {
//            forwarder[i] = system().actorOf(ForwarderActor.selection(forwarder[i - 1], i));
//        }
//
//        new Thread() {
//            @Override
//            public void run() {
//                Log.d("Start");
//                for (int i = 0; i < 10000; i++) {
//                    forwarder[9].send((Integer) i);
//                    if (i % 1000 == 0) {
//                        Log.d("Progress " + i);
//                        try {
//                            Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                Log.d("End");
//            }
//        }.start();
    }

    private void performBenchmark() {
//        timeBench1();
//        timeBench2();
//        timeBench3();
//        timeBench4();
//        timeBench5();
//        queueBench();
        queueBench();
        communcationBench();
    }

    private void timeBench1() {
        android.util.Log.d("ACTOR_BENCH", "-------- Staring time1 benchmark");
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            ActorTime.currentTime();
        }
        long duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * count + " p/s");
    }

    private void timeBench2() {
        android.util.Log.d("ACTOR_BENCH", "-------- Staring time2 benchmark");
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            System.nanoTime();
        }
        long duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * count + " p/s");
    }

    private void timeBench3() {
        android.util.Log.d("ACTOR_BENCH", "-------- Staring time3 benchmark");
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            SystemClock.uptimeMillis();
        }
        long duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * count + " p/s");
    }

    private void timeBench4() {
        android.util.Log.d("ACTOR_BENCH", "-------- Staring time4 benchmark");
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            System.currentTimeMillis();
        }
        long duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * count + " p/s");
    }

    private void timeBench5() {
        android.util.Log.d("ACTOR_BENCH", "-------- Staring time5 benchmark");
        int count = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            SystemClock.elapsedRealtime();
        }
        long duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * count + " p/s");
    }

    private void queueBench() {
        android.util.Log.d("ACTOR_BENCH", "-------- Staring queue benchmark");
        long start = System.currentTimeMillis();
        MailboxesQueue queue = new MailboxesQueue();
        Mailbox mailbox = new Mailbox(queue);
        android.util.Log.d("ACTOR_BENCH", "Created in: " + (System.currentTimeMillis() - start) + " ms");
        int count = 10000;
        long time = ActorTime.currentTime();
        for (int i = 0; i < count; i++) {
            mailbox.schedule(new Envelope(null, null, mailbox, null), time + i);
        }
        long duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * count + " p/s");

        android.util.Log.d("ACTOR_BENCH", "-------- Staring dispatch benchmark");
        start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            queue.dispatch(time);
        }
        duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * count + " p/s");
    }

    private void communcationBench() {
        android.util.Log.d("ACTOR_BENCH", "-------- Staring communication benchmark");
        long start = System.currentTimeMillis();
        ReceiverActor receiverActor = new ReceiverActor();
        ActorRef receiver = system().actorOf(testProps(receiverActor), "receiver");
        system().actorOf(testProps(new SenderActor(start, receiver)), "sender").send("!");
        receiverActor.waitEnd();
        long duration = (System.currentTimeMillis() - start);
        android.util.Log.d("ACTOR_BENCH", "Duration: " + duration + " ms");
        android.util.Log.d("ACTOR_BENCH", "Speed: " + (1000.0f / duration) * 10000 + " p/s");
    }

    private class ReceiverActor extends Actor {
        private Object lockObj = new Object();

        @Override
        public void onReceive(Object message) {
            if ("end".equals(message)) {
                Log.d("ACTOR_BENCH", "Ending in receiver");
                synchronized (lockObj) {
                    lockObj.notifyAll();
                }
            }
        }

        public void waitEnd() {
            synchronized (lockObj) {
                try {
                    lockObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private class SenderActor extends Actor {
        private ActorRef actorRef;
        long start;

        private SenderActor(long start, ActorRef actorRef) {
            this.start = start;
            this.actorRef = actorRef;
        }

        @Override
        public void preStart() {
            Log.d("ACTOR_BENCH", "Sender created in " + (System.currentTimeMillis() - start) + " ms");
            long sendStart = System.currentTimeMillis();
            for (int i = 0; i <= 10000; i++) {
                actorRef.send((Integer) i);
            }
            actorRef.send("end");
            Log.d("ACTOR_BENCH", "Sent in " + (System.currentTimeMillis() - sendStart) + " ms");
        }
    }

    private Props testProps(final Actor actor) {
        return Props.create(Actor.class, new ActorCreator<Actor>() {
            @Override
            public Actor create() {
                return actor;
            }
        });
    }
}