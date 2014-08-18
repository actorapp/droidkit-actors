package com.droidkit.actors.sample;

import com.droidkit.actors.Actor;
import com.droidkit.actors.tasks.TaskResult;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class DownloadFile extends Actor {

    @Override
    public void preStart() {
        Log.d("DownloadFile:preStart");
    }

    @Override
    public void onReceive(Object message) {
        Log.d("DownloadFile:onReceive:" + message);
        if (message instanceof String) {
            String url = (String) message;
            HttpDownloader.requestDownload(0, url, self());
        } else if (message instanceof TaskResult<?>) {
            byte[] data = (byte[]) ((TaskResult) message).getRes();
            Log.d("downloaded " + data.length + " bytes");
        }
    }
}
