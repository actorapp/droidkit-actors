package com.droidkit.actors.sample;

import com.droidkit.actors.*;
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

    public static String path(String url) {
        return "/http_" + HashUtil.md5(url);
    }

    public static Props<HttpDownloader> prop(final String url) {
        return Props.create(HttpDownloader.class, new ActorCreator<HttpDownloader>() {
            @Override
            public HttpDownloader create() {
                return new HttpDownloader(url);
            }
        });
    }

    public static ActorSelection download(String url) {
        return new ActorSelection(prop(url), path(url));
    }

    private String url;

    public HttpDownloader(String url) {
        this.url = url;
        setTimeOut(500);
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
                    byte[] data = IOUtils.readAll(in);
                    complete(data);
                } catch (IOException e) {
                    complete(new byte[0]);
                }
            }
        });
    }
}
