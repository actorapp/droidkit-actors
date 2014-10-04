package com.droidkit.actors.typed;

import com.droidkit.actors.ActorRef;
import com.droidkit.actors.concurrency.Future;
import com.droidkit.actors.typed.messages.TypedRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ex3ndr on 06.09.14.
 */
public class TypedCreator {
    public static <T> T typed(final ActorRef ref, Class<T> tClass) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{tClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        ClientFuture future = null;
                        if (method.getReturnType().equals(Future.class)) {
                            future = new ClientFuture();
                        }
                        ref.send(new TypedRequest(future, method, args));
                        return future;
                    }
                });
    }
}