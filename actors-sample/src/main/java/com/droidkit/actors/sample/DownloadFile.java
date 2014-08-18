package com.droidkit.actors.sample;

import com.droidkit.actors.Actor;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ReflectedActor;
import com.droidkit.actors.messages.PoisonPill;
import com.droidkit.actors.tasks.TaskResult;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class DownloadFile extends ReflectedActor {

    public void onReceive(String url) {
        Log.d("DownloadFile:onReceiveUrl:" + url);
        ActorRef ref = HttpDownloader.requestDownload(100, url, self());
        // ref.send(PoisonPill.INSTANCE, 100);
        // ref = HttpDownloader.requestDownload(100, url, self());
    }

    public void onReceive(TaskResult taskResult) {
        Log.d("DownloadFile:onReceiveTask:" + taskResult);
        byte[] data = (byte[]) taskResult.getRes();
        Log.d("downloaded " + data.length + " bytes");
    }
}
