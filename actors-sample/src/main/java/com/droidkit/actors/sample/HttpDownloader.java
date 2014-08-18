package com.droidkit.actors.sample;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSystem;
import com.droidkit.actors.Props;
import com.droidkit.actors.dispatch.RunnableDispatcher;
import com.droidkit.actors.tasks.TaskActor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.droidkit.actors.tasks.TaskRequest;

/**
 * Created by ex3ndr on 18.08.14.
 */
public class HttpDownloader extends TaskActor<byte[]> {

    private static final RunnableDispatcher dispatcher = new RunnableDispatcher(2);

    public static ActorRef requestDownload(int requestId, String url, ActorRef actorRef) {
        ActorRef ref = ActorSystem.system().actorOf(prop(url), "/http_" + HashUtil.md5(url));
        ref.send(new TaskRequest(requestId, actorRef));
        return ref;
    }

    public static Props<HttpDownloader> prop(final String url) {
        return Props.create(HttpDownloader.class, new ActorCreator<HttpDownloader>() {
            @Override
            public HttpDownloader create() {
                return new HttpDownloader(url);
            }
        });
    }

    private String url;

    public HttpDownloader(String url) {
        this.url = url;
        setTimeOut(500);
    }

    @Override
    public void onReceive(Object message) {
        Log.d("HttpDownloader:onReceive:" + message);
        super.onReceive(message);
    }

    @Override
    public void startTask() {
        Log.d("HttpDownloader:startTask:" + url);
        dispatcher.postAction(new Runnable() {
            @Override
            public void run() {
                try {
                    URL urlSpec = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) urlSpec.openConnection();
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setReadTimeout(15000);
                    InputStream in = urlConnection.getInputStream();
                    byte[] data = readAll(in);
                    complete(data);
                } catch (IOException e) {
                    complete(new byte[0]);
                }
            }
        });
    }

    @Override
    public void postStop() {
        Log.d("HttpDownloader:postStop");
    }

    public static byte[] readAll(InputStream in) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4 * 1024];
        int len;
        int readed = 0;
        try {
            while ((len = bufferedInputStream.read(buffer)) >= 0) {
                Thread.yield();
                os.write(buffer, 0, len);
                readed += len;
            }
        } catch (java.io.IOException e) {

        }
        return os.toByteArray();
    }
}
