package com.droidkit.actors.sample;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ReflectedActor;
import com.droidkit.actors.tasks.AskCallback;
import com.droidkit.actors.tasks.TaskResult;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class DownloadFile extends ReflectedActor {

    public void onReceive(String[] url) {
        Log.d("DownloadFile:onReceiveUrls:" + url);
        final byte[][] res = new byte[2][];

        ask(HttpDownloader.download(url[0]), new AskCallback<byte[]>() {
            @Override
            public void onResult(byte[] result) {
                res[0] = result;
                if (res[1] != null) {
                    self().send(res);
                }
                // self().send(result);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });

        ask(HttpDownloader.download(url[1]), new AskCallback<byte[]>() {
            @Override
            public void onResult(byte[] result) {
                res[1] = result;
                if (res[0] != null) {
                    self().send(res);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    public void onReceive(String url) {
        Log.d("DownloadFile:onReceiveUrl:" + url);
        ask(HttpDownloader.download(url), new AskCallback() {
            @Override
            public void onResult(Object result) {
                self().send(result);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    public void onReceive(byte[] data) {
        Log.d("DownloadFile:receiveData:" + data);
        Log.d("downloaded " + data.length + " bytes");
    }

    public void onReceive(byte[][] data) {
        Log.d("DownloadFile:receiveDatas:" + data);
        Log.d("downloaded " + data[0].length + " bytes and " + data[1].length + " bytes");
    }
}